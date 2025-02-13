package utils

import io.circe.syntax.EncoderOps
import io.circe._
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import scala.jdk.CollectionConverters.{ListHasAsScala, MapHasAsJava, MapHasAsScala, SeqHasAsJava}

object DDBJsonUtils {
  object AttributeValueEncoderDecoder {

    implicit val encodeAttributeValueMap: Encoder[java.util.Map[String, AttributeValue]] = Encoder.instance { map =>
      Json.obj(
        map.asScala.map { case (key, value) =>
          key -> encodeAttributeValue(value)
        }.toSeq: _*
      )
    }

    implicit val encodeAttributeValue: Encoder[AttributeValue] = Encoder.instance {

      case value if value.nul() != null && value.nul() =>
        Json.Null

      case value if value.s() != null =>
        Json.fromString(value.s())

      case value if value.n() != null =>
        val numberString = value.n()
        if (numberString.contains(".")) {
          Json.fromDouble(numberString.toDouble).get
        } else {
          Json.fromLong(numberString.toLong)
        }

      case value if value.bool() != null =>
        Json.fromBoolean(value.bool())

      case value if value.hasM =>
        value.m().asJson

      case value if value.hasL =>
        Json.fromValues(value.l().asScala.map(encodeAttributeValue(_)))

      case value if value.b() != null =>
        Json.fromString(value.b().asByteArray().map("%02x".format(_)).mkString)

      case _ => Json.Null
    }

    implicit val decodeAttributeValue: Decoder[AttributeValue] = Decoder.instance { cursor: HCursor =>
      cursor.focus.get

      cursor.focus match {
        case None => Right(AttributeValue.builder().nul(true).build())

        case Some(json) =>
          if (json.isNull) {
            Right(AttributeValue.builder().nul(true).build())
          } else if (json.isString)
            Right(AttributeValue.builder().s(json.asString.get).build())
          else if (json.isNumber)
            Right(AttributeValue.builder().n(json.asNumber.get.toBigDecimal.get.toString).build())
          else if (json.isBoolean)
            Right(AttributeValue.builder().bool(json.asBoolean.get).build())
          else if (json.isArray) {
            val items = json.asArray.get
            val attributeValues = items.map(item => decodeAttributeValue(item.hcursor)).collect { case Right(av) =>
              av
            }
            Right(AttributeValue.builder().l(attributeValues.asJava).build())

          } else if (json.isObject) {
            val attributeValues = json.asObject.get.toMap.view.mapValues(value => decodeAttributeValue(value.hcursor))
            val validValues = attributeValues.collect { case (k, Right(v)) =>
              k -> v
            }
            Right(AttributeValue.builder().m(validValues.toMap.asJava).build())
          } else {
            Left(DecodingFailure("Unsupported JSON type for AttributeValue", cursor.history))
          }
      }
    }

    implicit val decodeAttributeValueMap: Decoder[java.util.Map[String, AttributeValue]] = Decoder.instance {
      cursor: HCursor =>
        cursor.as[Map[String, AttributeValue]].map(_.asJava)
    }

  }

}

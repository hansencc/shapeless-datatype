package shapeless.datatype.datastore

import org.scalacheck.Prop.{all, forAll}
import org.scalacheck.Shapeless._
import org.scalacheck._
import shapeless._
import shapeless.datatype.SerializableUtils
import shapeless.datatype.record._

class DatastoreTypeSpec extends Properties("DatastoreType") {

  import shapeless.datatype.Records._

  implicit def compareByteArrays(x: Array[Byte], y: Array[Byte]) = java.util.Arrays.equals(x, y)
  implicit def compareIntArrays(x: Array[Int], y: Array[Int]) = java.util.Arrays.equals(x, y)

  def roundTrip[A, L <: HList](m: A, t: DatastoreType[A] = DatastoreType[A])
                              (implicit
                               gen: LabelledGeneric.Aux[A, L],
                               fromL: FromEntity[L],
                               toL: ToEntity[L],
                               rm: RecordMatcher[L]): Prop = {
    val rmt = RecordMatcherType[A]
    all(
      t.fromEntity(t.toEntity(m)).exists(rmt(_, m)),
      t.fromEntityBuilder(t.toEntityBuilder(m)).exists(rmt(_, m))
    )
  }

  property("required") = forAll { m: Required => roundTrip(m) }
  property("optional") = forAll { m: Optional => roundTrip(m) }
  property("repeated") = forAll { m: Repeated => roundTrip(m) }
  property("mixed") = forAll { m: Mixed => roundTrip(m) }
  property("nested") = forAll { m: Nested => roundTrip(m) }
  property("seq types") = forAll { m: SeqTypes => roundTrip(m) }

  val t = SerializableUtils.ensureSerializable(DatastoreType[Nested])
  property("serializable") = forAll { m: Nested => roundTrip(m, t) }

}

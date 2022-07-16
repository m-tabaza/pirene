package pirene.compiler

import pirene.util.given

class TypeReprTest extends munit.FunSuite {
  quoted.staging.run {
    val t = new TypeImpl
    import t.quotes.reflect.*

    test("Primitive types are represented correctly") {
      assert {
        t.repr[Long] =:= TypeRepr.of[Long]
      }

      assert {
        t.repr[Unit] =:= TypeRepr.of[Unit]
      }

      assert {
        t.repr[String] =:= TypeRepr.of[String]
      }
    }

    test("Function types are represented correctly") {
      assert {
        t.function(TypeRepr.of[Long], TypeRepr.of[String]) =:=
          TypeRepr.of[Long => String]
      }

      assert {
        t.curriedFunction(
          t.repr[Long],
          t.repr[String],
          t.repr[Boolean],
          t.repr[Unit]
        ) =:= TypeRepr.of[Long => String => Boolean => Unit]
      }
    }

    test("Tuples are represented correctly") {
      assert {
        t.tuple(t.repr[Long], t.repr[String]) =:=
          TypeRepr.of[(Long, String)]
      }

      assert {
        t.tuple(t.repr[Long], t.repr[String], t.repr[Boolean], t.repr[Unit]) =:=
          TypeRepr.of[(Long, String, Boolean, Unit)]
      }
    }

    test("Records are represented correctly") {
      val record = t.record(
        "x" -> t.repr[Long],
        "y" -> t.repr[String],
        "z" -> t.repr[Vector[String]]
      )

      assert {
        record =:=
          TypeRepr.of[{ val x: Long; val z: Vector[String]; val y: String }]
      }

      assert {
        record <:< TypeRepr.of[{ val x: Long }]
      }
    }

    test("Unions are represented correctly") {
      val u = t.union(t.repr[String], t.repr[Boolean], t.repr[Long])

      assert {
        u =:= TypeRepr.of[Long | String | Boolean]
      }

      assert {
        TypeRepr.of[Long] <:< u
      }

      assert {
        !(u <:< TypeRepr.of[Long])
      }
    }

    test("Type application is represented correctly") {
      assert {
        t.applicable(t.repr[[X] =>> List[X]]).isDefined
      }

      assert {
        t.applicable(t.repr[List]).isDefined
      }

      assert {
        t.applicable(t.repr[Int]).isEmpty
      }
    }

    '{ () }
  }
}

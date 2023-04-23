package pirene.compiler

type Program[F[_], Value] = List[Value] => F[Value]

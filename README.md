# atom-tin

Tiny Scala API to stream, parse, and cache atom coordinates from Protein Data Bank files. Only _ATOM_ and _HETATM_ records are parsed. Uses [ScalaCache](https://github.com/cb372/scalacache) as a facade to support virtually any caching backend, such as [Ehcache](https://github.com/ehcache), [Redis](https://github.com/antirez/redis), [Caffeine](https://github.com/ben-manes/caffeine), or a custom backend.

Provides a model (`PdbAtom`), a parser (`PdbParser`), and a cache (`AtomTin`). There are three SBT subprojects:
- _core_, which includes only the parser, model, and cache
- _caffeinated_, which uses Caffeine for in-memory caching (via `CaffeinatedAtomTin`)
- _pickled_, which uses [Pickling](https://github.com/scala/pickling) for on-disk serialization (via `PickledAtomTin`)

### Examples

Simple example of using _pickled_ and synchronous lookups:

```scala
val tin = new PickledAtomTin() // directory for serialization defaults to ~/atom-tin-cache
val atoms: TraversableOnce[PdbAtom] = tin.loadAndWait("1hiv") // max wait defaults to infinite
```

Or the same with _caffeinated_:

```scala
val tin = new CaffeinatedAtomTin(_.maximumSize(100)) // alter Caffeine defaults for maximumSize
val atoms: TraversableOnce[PdbAtom] = tin.loadAndWait("1hiv")
```

To use asynchronous lookups, use:
```scala
val atoms: Future[TraversableOnce[PdbAtom]] = tin.load("1hiv")
```

Here's a more complex example that prints the coordinates of Arginines for multiple PDB structures asynchronously:
```scala
def printArginines(pdbId: String) = {
	tin.load(pdbId) map {
		atoms => atoms filter (a => a.residueName == Right(AminoAcid.Arginine))
				map (_.coordinates)
	} onSuccess {
		case coordinates => println(coordinates)
	}
}
Seq("1HIV", "5AYR", "2D26", "3JD6") map printArginines
```

You can bypass the cache, delete items from the cache, or clear the cache:
```scala
AtomTin.download("1hiv") // skips the cache
cache.delete("1hiv") // removes 1hiv from the cache
cache.deleteAll() // clears the cache
```

### Notes

- The parser is covered by [ScalaCheck](https://www.scalacheck.org/) property tests. The caching currently lacks tests but seems to work.
- `AtomTin` and its subclasses require an implicit `ExecutionContext`.
- `PickledAtomTin` is currently limited to Gzipped JSON.

### License

The software is licensed under the Apache License, Version 2.0 by Douglas Myers-Turnbull.

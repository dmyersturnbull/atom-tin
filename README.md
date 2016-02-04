# atom-tin

Tiny Scala API to stream, parse, and cache atom coordinates from Protein Data Bank files. Only _ATOM_ and _HETATM_ records are parsed.

This project is currently under active development and has limited test coverage and poor documentation.

Uses [ScalaCache](https://github.com/cb372/scalacache) as a facade to support virtually any caching backend, such as [Ehcache](https://github.com/ehcache), [Redis](https://github.com/antirez/redis), [Caffeine](https://github.com/ben-manes/caffeine), or a custom backend.

Contains three SBT subprojects:
- _core_, which includes only the parsers, model, and `AtomTin`
- _caffeinated_, containing `CaffeinatedAtomTin` and which uses Caffeine dependency for in-memory caches
- _pickled_, containing `PickledAtomTin` and which uses [Pickling](https://github.com/scala/pickling) for on-disk serialization

Here's an example of using _caffeinated_:

```scala
val tin = new PickledAtomTin() // directory for serialization defaults to ~/atom-tin-cache
val atomsFuture: Future[TraversableOnce[PdbAtom]] = tin.load("1hiv")
val atomsNow: TraversableOnce[PdbAtom] = tin.loadAndWait("1hiv")
```

### Notes

- `AtomTin` and its subclasses require an implicit `ExecutionContext`.
- `PickledAtomTin` is currently limited to Gzipped JSON.

### License

The software is licensed under the Apache License, Version 2.0 by Douglas Myers-Turnbull.
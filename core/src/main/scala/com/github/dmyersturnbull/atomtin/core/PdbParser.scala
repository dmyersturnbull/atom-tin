/*
   Copyright 2016 Douglas Myers-Turnbull

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.github.dmyersturnbull.atomtin.core

import com.github.dmyersturnbull.atomtin.core.model._
import org.slf4j.LoggerFactory

/**
  * Transforms a String into a PdbAtom.
  *
  * @param warn: Logs warnings through SLF4J for problems listed in the PDB, namely OBSLETE and CAVEAT records
  * @author Douglas Myers-Turnbull
  */
class PdbParser(warn: Boolean = true) extends ((String) => PdbAtom) {

	protected val logger = LoggerFactory.getLogger(classOf[PdbParser])

	def parse(lines: TraversableOnce[String]): TraversableOnce[PdbAtom] = {
		def warner(lines: TraversableOnce[String]): TraversableOnce[String] = {
			var pdbId: Option[String] = None
			lines map {line =>
				line.substring(0, 6) match {
					case "HEADER" => pdbId = Some(line.substring(62, 66).trim)
					case "OBSLTE" => logger.warn("Entry identifying itself as {} is obsolete", pdbId.getOrElse("<unknown>"))
					// TODO ambiguous ref compile error with logger.warn(String, String, String)
					case "CAVEAT" => logger.warn(String.format("Entry identifying itself as %s has caveat: \"%s\"", pdbId.getOrElse("<unknown>"), line.substring(19, 79).trim))
				}
				line // use map rather than foreach so we can chain to parser with Iterator/TraversableOnce
			}
		}
		(if (warn) warner(lines) else lines) filter (s => s.startsWith("ATOM") || s.startsWith("HETATM")) map this
	}

	override def apply(line: String): PdbAtom = {
		if (line.length < 80 || line.substring(0, 6) != "ATOM  " && line.substring(0, 6) != "HETATM") {
			throw new IllegalArgumentException("Invalid PDB ATOM or HETATM line \"" + line + "\"")
		}
		val aa = line.substring(18-1, 20).trim
		val e = line.substring(77-1, 78).trim
		try {
			PdbAtom(
				id = line.substring(7-1, 11).trim.toInt,
				residueName = AminoAcid byThreeLetter aa toRight aa,
				chainId = line.charAt(22-1),
				residueId = PdbResidueId(line.substring(23-1, 26).trim.toInt, line.charAt(27-1)),
				coordinates = PdbCoordinates(
					BigDecimal.exact(line.substring(31-1, 38).trim),
					BigDecimal.exact(line.trim.substring(39-1, 46)),
					BigDecimal.exact(line.trim.substring(47-1, 54))
				),
				element = AtomicElement bySymbol e toRight e,
				charge = Option(line.substring(79-1, 80).trim) filter (_.nonEmpty)
			)
		} catch {
			case e: NumberFormatException => throw new IllegalArgumentException(e)
			case e: Throwable =>
				e.addSuppressed(new Exception("Failed to parse PDB line \"" + line + "\""))
				throw e
		}
	}

}

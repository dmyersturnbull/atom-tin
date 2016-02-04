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

/**
  * Transforms a String into a PdbAtom.
  * @author Douglas Myers-Turnbull
  */
class PdbParser extends ((String) => PdbAtom) {

	override def apply(line: String) = {
		if (line.length < 80 || line.substring(0, 6) != "ATOM  " && line.substring(0, 6) != "HETATM") {
			throw new IllegalArgumentException("Invalid PDB ATOM or HETATM line \"" + line + "\"")
		}
		val aa = line.substring(17, 20).trim
		val e = line.substring(76, 78).trim
		try {
			PdbAtom(
				id = line.substring(6, 11).trim.toInt,
				residueName = AminoAcid byThreeLetter aa toRight aa,
				chainId = line.charAt(21),
				residueId = PdbResidueId(line.substring(22, 26).trim.toInt, line.charAt(26)),
				coordinates = PdbCoordinates(line.substring(30, 38).trim.toFloat, line.trim.substring(39, 46).toFloat, line.trim.substring(46, 54).toFloat),
				element = AtomicElement bySymbol e toRight e,
				charge = Option(line.substring(78, 80).trim) filter (_.nonEmpty)
			)
		} catch {
			case e: NumberFormatException => throw new IllegalArgumentException(e)
			case e: Throwable =>
				e.addSuppressed(new Exception("Failed to parse PDB line \"" + line + "\""))
				throw e
		}
	}

}

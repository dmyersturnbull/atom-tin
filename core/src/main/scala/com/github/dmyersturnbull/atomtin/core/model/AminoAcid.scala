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

package com.github.dmyersturnbull.atomtin.core.model

import scala.collection.mutable

/**
  * The proteinogenic amino acids.
  * @author Douglas Myers-Turnbull
  */
object AminoAcid {

	var allTypes = Seq[Type]()

	private val names = new mutable.HashMap[String, Type]()
	private val threeLetters = new mutable.HashMap[String, Type]()
	private val oneLetters = new mutable.HashMap[Char, Type]()

	val Arginine = value("arginine", "Arg", 'R')
	val Lysine = value("lysine", "Lys", 'K')
	val AsparticAcid = value("aspartic acid", "Asp", 'D')
	val GlutamicAcid = value("glutamic acid", "Glu", 'E')
	val Glutamine = value("glutamine", "Gln", 'Q')
	val Asparagine = value("asparagine", "Asn", 'N')
	val Histidine = value("histidine", "His", 'H')
	val Serine = value("serine", "Ser", 'S')
	val Threonine = value("threonine", "Thr", 'T')
	val Tyrosine = value("tyrosine", "Tyr", 'Y')
	val Methionine = value("methionine", "Met", 'M')
	val Tryptophan = value("tryptophan", "Trp", 'W')
	val Alanine = value("alanine", "Ala", 'A')
	val Isoleucine = value("isoleucine", "Ile", 'I')
	val Leucine = value("leucine", "Leu", 'L')
	val Phenylalanine = value("phenylalanine", "Phe", 'F')
	val Valine = value("valine", "Val", 'V')
	val Proline = value("proline", "Pro", 'P')
	val Glycine = value("glycine", "Gly", 'G')
	val Cysteine = value("cysteine", "Cys", 'C')
	val Selenocysteine = value("selenocysteine", "Sec", 'U')
	val Pyrrolysine = value("pyrrolysine", "Pyl", 'O')

	def byName(name: String) = names get name.toUpperCase
	def byThreeLetter(threeLetter: String) = threeLetters get threeLetter.toUpperCase
	def byOneLetter(oneLetter: Char) = oneLetters get oneLetter.toUpper

	sealed case class Type(name: String, threeLetterCode: String, oneLetterCode: Char)

	protected final def value(name: String, threeLetterCode: String, oneLetterCode: Char) = {
		val aa = Type(name, threeLetterCode, oneLetterCode)
		names.put(name.toUpperCase, aa)
		oneLetters.put(oneLetterCode.toUpper, aa)
		threeLetters.put(threeLetterCode.toUpperCase, aa)
		allTypes :+= aa
		aa
	}

}

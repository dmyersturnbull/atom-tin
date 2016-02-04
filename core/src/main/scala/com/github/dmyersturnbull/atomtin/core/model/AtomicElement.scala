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
  * Elements commonly found in PDB structures.
  * @author Douglas Myers-Turnbull
  */
object AtomicElement {

	private val names = new mutable.HashMap[String, Type]()
	private val symbols = new mutable.HashMap[String, Type]()
	private val atomicNumbers = new mutable.HashMap[Short, Type]()

	val Hydrogen = value("hydrogen", "H", 1)
	val Carbon = value("carbon", "C", 6)
	val Nitrogen = value("nitrogen", "N", 7)
	val Oxygen = value("oxygen", "O", 8)
	val Sodium = value("sodium", "Na", 11)
	val Magnesium = value("magnesium", "Mg", 12)
	val Phosphorus = value("phosphorus", "P", 15)
	val Sulfur = value("sulfur", "S", 16)
	val Chlorine = value("chlorine", "Cl", 17)
	val Potassium = value("potassium", "K", 19)
	val Calcium = value("calcium", "Ca", 20)
	val Iron = value("iron", "Fe", 26)
	val Zinc = value("zinc", "Zn", 30)

	def byName(name: String) = names get name
	def bySymbol(symbol: String) = symbols get symbol
	def byAtomicNumber(number: Short) = atomicNumbers get number

	sealed case class Type(name: String, symbol: String, number: Short)
	protected final def value(name: String, symbol: String, number: Short) = {
		val e = Type(name, symbol, number)
		names.put(name, e)
		symbols.put(symbol, e)
		atomicNumbers.put(number, e)
	}

}

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

/**
  * R&#94;3 as Floats, a point an atom resides in.
  * FLoats are used because the PDB format's fixed-width nature limits the precision.
  * @author Douglas Myers-Turnbull
  */
case class PdbCoordinates(x: BigDecimal, y: BigDecimal, z: BigDecimal) {

	override def toString = "(" + x + "," + y + "," + z + ")"

	/**
	  * Element-wise addition.
	  */
	def +(that: PdbCoordinates) = new PdbCoordinates(x + that.x, y + that.y, z + that.z)

	/**
	  * Element-wise subtraction.
	  */
	def -(that: PdbCoordinates) = new PdbCoordinates(x - that.x, y - that.y, z - that.z)

	def *(scalar: Double): PdbCoordinates = *(BigDecimal.valueOf(scalar))
	def *(scalar: BigDecimal): PdbCoordinates = new PdbCoordinates(scalar * x, scalar * y, scalar * z)

	/**
	  * Element-wise multiplication.
	  */
	def :*(that: PdbCoordinates) = new PdbCoordinates(that.x * x, that.y * y, that.z * z)

	/**
	  * Dot product.
	  */
	def dot(that: PdbCoordinates) = that.x * x + that.y * y + that.z * z
}

package com.github.dmyersturnbull.atomtin

import com.github.dmyersturnbull.atomtin.core.PdbParser
import com.github.dmyersturnbull.atomtin.core.model.{PdbResidueId, PdbAtom, PdbCoordinates, AminoAcid}
import org.scalacheck.Gen
import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.PropertyChecks

/**
  * ScalaCheck property tests for PdbParser.
  */
class PdbParserTest extends PropSpec with PropertyChecks with Matchers {

	private implicit class Paddable(string: String) {
		def ^(end: Int): String = " " * (end - string.length - 1) + string
		def $(end: Int): String = string + " " * (end - string.length - 1)
	}

	case class Line(str: String, atom: PdbAtom) // line and answer

	def int(n: Int): Gen[String] = Gen.listOfN(n, Gen.numChar) map (_.mkString)
	def char(): Gen[Char] = Gen.alphaUpperChar
	def aa = Gen.oneOf[AminoAcid.Type](AminoAcid.allTypes) // currently unused
	def str(n: Int): Gen[String] = Gen.listOfN(n, Gen.alphaUpperChar) map (_.mkString)
	def float(length: Int, nDecimalPlaces: Int): Gen[String] = for {
		x <- int(length - nDecimalPlaces - 1) // -1 is for '.'
		y <- int(nDecimalPlaces)
	} yield x + "." + y

	implicit val generator: Gen[Line] = for {

		serialNumber <- int(11-7+1)
		atomName <- str(16-13+1)
		altLoc <- char()
		resName <- str(20-18+1) // could instead be AA
		chainId <- char()
		resSeq <- int(26-23+1)
		insCode <- char()
		x <- float(8, 3)
		y <- float(8, 3)
		z <- float(8, 3)
		occupancy <- float(6, 2)
		tempFactor <- float(6, 2)
		element <- str(2)
		charge <- str(2)

	/*
	COLUMNS        DATA  TYPE    FIELD        DEFINITION
	-------------------------------------------------------------------------------------
	 1 -  6        Record name   "ATOM  "
	 7 - 11        Integer       serial       Atom  serial number.
	13 - 16        Atom          name         Atom name.
	17             Character     altLoc       Alternate location indicator.
	18 - 20        Residue name  resName      Residue name.
	22             Character     chainID      Chain identifier.
	23 - 26        Integer       resSeq       Residue sequence number.
	27             AChar         iCode        Code for insertion of residues.
	31 - 38        Real(8.3)     x            Orthogonal coordinates for X in Angstroms.
	39 - 46        Real(8.3)     y            Orthogonal coordinates for Y in Angstroms.
	47 - 54        Real(8.3)     z            Orthogonal coordinates for Z in Angstroms.
	55 - 60        Real(6.2)     occupancy    Occupancy.
	61 - 66        Real(6.2)     tempFactor   Temperature  factor.
	77 - 78        LString(2)    element      Element symbol, right-justified.
	79 - 80        LString(2)    charge       Charge  on the atom.
			 1         2         3         4         5         6         7         8
	12345678901234567890123456789012345678901234567890123456789012345678901234567890
	ATOM     32  N  AARG A  -3      11.281  86.699  94.383  0.50 35.88           N
	*/

	} yield {

		// all positions are 1-based, like in the spec. $ and ^ functions subtract 1 from n.
		// this is the ugliest format in the world
		// note that some elements are left-aligned and others are right-aligned

		// "ATOM" is left-aligned, but serialNumber is right-aligned
		var line = ("ATOM" $ 7) + serialNumber $ 13 // there's a gap at position 12

		// it's ambiguous whether atomName is right- or left- aligned; it seems to be centered in the example
		line = line + atomName $ 16

		// right-aligned
		line = line + altLoc // 1 char
		line = line + resName $ 22 // there's a gap at 21 (21 is ' ')
		line = line + chainId // 1 char
		line = line + resSeq $ 26
		line = line + insCode // 1 char

		// right-aligned floats
		// a gap between 27 and 31, exclusive
		line = (line $ 31) + x
		line = (line $ 39) + y
		line = (line $ 47) + z
		line = (line $ 55) + occupancy
		line = (line $ 61) + tempFactor

		// there's a gap between 66 and 77, exclusive
		line = line $ 77

		// left-aligned strings (LString)
		line = line + element $ 78
		line = line + charge $ 80

		Line(line, new PdbAtom(
			serialNumber.toInt,
			Left(resName),
			new PdbCoordinates(BigDecimal.exact(x), BigDecimal.exact(y), BigDecimal.exact(z)),
			chainId,
			new PdbResidueId(resSeq.toInt, insCode),
			Left(element),
			Some(charge))
		)

	}

	property("Can parse arbitrary valid PDB ATOM lines") {
		val parser = new PdbParser()
		forAll(generator) { (line: Line) => {
			val atom = parser.apply(line.str)
			line.atom should equal(atom)
		}}
	}


}
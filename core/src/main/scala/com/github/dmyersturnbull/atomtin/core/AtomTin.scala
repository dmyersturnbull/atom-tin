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

import java.io._
import java.net.URL
import java.util.zip.GZIPInputStream

import org.slf4j.LoggerFactory

import scala.io.Source
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

import com.github.dmyersturnbull.atomtin.core.model.PdbAtom

import scalacache._

/**
  * @author Douglas Myers-Turnbull
  */
class AtomTin(cache: ScalaCache, source: String => TraversableOnce[PdbAtom] = pdbId => AtomTin.download(pdbId, warn = true))
			 (implicit ec: ExecutionContext) extends Object with Closeable {

	implicit val scalaCache = cache

	protected val logger = LoggerFactory.getLogger(classOf[AtomTin])

	def loadAndWait(pdbId: String, duration: Duration = Duration.Inf): TraversableOnce[PdbAtom] =  {
		val r = load(pdbId)
		Await.ready(r, duration)
		r.value.get.get // :(
	}

	def load(pdbId: String): Future[TraversableOnce[PdbAtom]] = {
		// TODO Why can't I just use caching()?
			get(pdbId).asInstanceOf[Future[Option[TraversableOnce[PdbAtom]]]] map {
			case Some(atoms) => atoms
			case None =>
				logger.debug("Retrieving atoms for {} from source...", pdbId)
				val r = source(pdbId)
				logger.debug("Done. Adding atoms for {} to cache.", pdbId)
				put(pdbId)(r) // ditto
				r
		}
	}

	override def close() {
		cache.cache.close()
	}

	def delete(pdbId: String) {
		remove(pdbId)
		logger.debug("Deleted {} from cache", pdbId)
	}

	def deleteAll() {
		removeAll()
		logger.debug("Deleted all entries in cache")
	}
}

object AtomTin {

	protected val logger = LoggerFactory.getLogger(classOf[AtomTin])

	def urlFor(pdbId: String) = new URL("http://www.rcsb.org/pdb/files/" + pdbId.toUpperCase + ".pdb.gz")

	def download(pdbId: String): TraversableOnce[PdbAtom] = download(pdbId, warn=true)
	def download(pdbId: String, warn: Boolean): TraversableOnce[PdbAtom] = {
		val is = new GZIPInputStream(urlFor(pdbId).openStream())
		new PdbParser(warn = warn).parse(
			Source.fromInputStream(is) withClose (() => is.close()) getLines()
		)
	}

}
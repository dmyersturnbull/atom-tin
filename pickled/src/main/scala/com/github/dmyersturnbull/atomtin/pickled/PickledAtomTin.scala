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

package com.github.dmyersturnbull.atomtin.pickled

import java.io._
import java.util.zip.{GZIPOutputStream, GZIPInputStream}

import com.github.dmyersturnbull.atomtin.core.AtomTin
import com.github.dmyersturnbull.atomtin.core.model.PdbAtom

import scala.concurrent._
import scala.io.Source
import scala.concurrent.duration.Duration
import scalacache.{ScalaCache, Cache}
import scala.pickling.Defaults._
import scala.pickling.json._
import scala.pickling.shareNothing._
import ExecutionContext.Implicits.global

/**
  * Created by student on 1/31/16.
  */
class PickledAtomTin(storeDir: File = new File(System.getProperty("user.home"), "atom-tin-cache"),
					 clearOnExit: Boolean = false,
					 source: String => TraversableOnce[PdbAtom] = AtomTin.download)
		extends AtomTin( {

	new ScalaCache(
	new Cache() {

		import scala.pickling.json._
		val filenameExtension = ".pdb-atoms.json.gz"

		def pathOf(pdbId: String) = new File(storeDir, pdbId + filenameExtension)

		override def get[V](pdbId: String): Future[Option[V]] = Future.successful {
			val filename = pathOf(pdbId)
			if (!storeDir.exists()) storeDir.mkdir()
			val q = storeDir.listFiles exists (_.getPath == filename.getPath) match {
				case false => None
				//				case true => Some(Source.fromFile(filename).mkString.unpickle[Seq[PdbAtom]]) // for JSON
				case true => Some({
					val is = new GZIPInputStream(new FileInputStream(filename))
					(scala.io.Source.fromInputStream(is)
							withClose (() => is.close()))
							.mkString.unpickle[Seq[PdbAtom]]
				})
			}
			q.asInstanceOf[Option[V]]
		}

		override def removeAll(): Future[Unit] = Future.successful {
			(storeDir.listFiles
			filter (_.getName.endsWith(filenameExtension))
			foreach (_.delete()))
		}

		override def put[V](id: String, value: V, ttl: Option[Duration]) = try {
			Future.successful {
				val filename = pathOf(id)
				val pw = new PrintWriter(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(filename))))
				try {
					pw.print(value.asInstanceOf[TraversableOnce[PdbAtom]].toSeq.pickle.value)
					pw.flush()
				} catch {
					case e: RuntimeException =>
						if (filename.exists()) filename.delete()
						throw e
				} finally {
					pw.close()
				}
			}
		}

		override def remove(pdbId: String) = Future.successful {
			pathOf(pdbId).delete()
		} map (s => Unit)

		override def close() {
			if (clearOnExit) removeAll()
		}
	})

}, source = source)

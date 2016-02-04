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

package com.github.dmyersturnbull.atomtin.caffeine

import com.github.dmyersturnbull.atomtin.core.AtomTin

import scala.concurrent.ExecutionContext
import scalacache.caffeine.CaffeineCache

import scalacache._
import com.github.benmanes.caffeine.cache.{Cache, Caffeine}

/**
  * An AtomTin that uses Caffeine as an in-memory cache.
  *
  * @author Douglas Myers-Turnbull
  */
class CaffeinatedAtomTin(modifier: Caffeine[String, Object] => Caffeine[String, Object] = Predef.identity)
		extends AtomTin({
			val coffee = Caffeine.newBuilder().asInstanceOf[Caffeine[String, Object]] // could put defaults here
			val cache: Cache[String, Object] = modifier(coffee).build()
			ScalaCache(CaffeineCache(cache))
		})(ExecutionContext.global)

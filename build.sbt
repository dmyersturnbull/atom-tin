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

name := "atom-tin"

lazy val commonSettings = Seq(
	organization := "com.github.dmyersturnbull",
	version := "0.1",
	scalaVersion := "2.11.7",
	javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:all"),
	scalacOptions ++= Seq("-unchecked", "-deprecation"),
	testOptions in Test += Tests.Argument("-oF"),
	test in assembly := {},
	assemblyJarName in assembly := name.value + ".jar",
	target in assembly := file("target"),
	libraryDependencies ++= Seq(
		"org.slf4j" % "slf4j-api" % "1.7.13",
		"com.github.cb372" %% "scalacache-core" % "0.7.5",
		"org.mockito" % "mockito-core" % "2.0.36-beta" % "test",
		"org.scalatest" % "scalatest_2.10" % "2.0" % "test"
	),
	pomExtra :=
		<url>https://github.com/dmyersturnbull/atom-tin</url>
		<licenses>
			<license>
				<name>Apache License, Version 2.0</name>
				<url>http://www.apache.org/licenses/LICENSE-2.0</url>
			</license>
		</licenses>
		<scm>
			<url>https://github.com/dmyersturnbull/atom-tin</url>
			<connection>https://github.com/dmyersturnbull/atom-tin.git</connection>
		</scm>
		<developers>
			<developer>
				<id>dmyersturnbull</id>
				<name>Douglas Myers-Turnbull</name>
				<url>https://www.dmyersturnbull.com</url>
				<timezone>-8</timezone>
			</developer>
		</developers>
		<issueManagement>
			<system>Github</system>
			<url>https://github.com/dmyersturnbull/atom-tin/issues</url>
		</issueManagement>
)

lazy val core = project.
		settings(commonSettings: _*)

lazy val caffeinated = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val pickled = project.
		settings(commonSettings: _*).
		dependsOn(core)

lazy val root = (project in file(".")).
		settings(commonSettings: _*).
		aggregate(core, caffeinated)

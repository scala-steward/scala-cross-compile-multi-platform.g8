import sbtcrossproject.CrossProject

val Scala212 = "2.12.17"
val Scala213 = "2.13.10"
val Scala3 = "3.2.2"

ThisBuild / crossScalaVersions := Seq(Scala3, Scala213, Scala213)

enablePlugins(ScalaNativePlugin)

// set to Debug for compilation details (Info is default)
logLevel := Level.Info

// import to add Scala Native options
import scala.scalanative.build._

lazy val root = project
  .in(file("."))
  .aggregate(rootJVM, rootJS, rootNative)
  .settings(
    name := "root"
  )

lazy val rootNative =
  project.aggregate(nativeProjects: _*)

lazy val rootJS =
  project.aggregate(jsProjects: _*)

lazy val rootJVM =
  project.aggregate(jvmProjects: _*)

lazy val core =
  crossProject(JSPlatform, JVMPlatform, NativePlatform).in(file("core"))

// TODO
lazy val example = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("example"))
  .dependsOn(core)
  .settings(name := "example")
  .jsSettings(scalaJSUseMainModuleInitializer := true)

val nativeProjects: Seq[ProjectReference] =
  Seq(
    core.native
  )

val jsProjects: Seq[ProjectReference] =
  Seq(
    core.js
  )

val jvmProjects: Seq[ProjectReference] =
  Seq(
    core.jvm
  )

// defaults set with common options shown
nativeConfig ~= { c =>
  c.withLTO(LTO.none) // thin
    .withMode(Mode.debug) // releaseFast
    .withGC(GC.immix) // commix
}

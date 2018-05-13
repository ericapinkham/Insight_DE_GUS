
import Jobs.Extractor.CommitRecord.{extractLanguage, extractPackages}
import org.scalatest.FunSuite

class CommitRecordTestSuite extends FunSuite {
  // placeholder for unit testing.

  test("Language Extraction 01") {
    assert(extractLanguage("hello_world.py") === "Python")
    assert(extractLanguage("     hello_world.hs       ") === "Haskell")
    assert(extractLanguage("hello_world.scala\n") === "Scala")
    assert(extractLanguage("hello_world.java\r\n") === "Java")
    assert(extractLanguage("hello_world.js") === "JavaScript")
  }
  
  test("Python Packages") {
    // Python
    val pythonImports =
      """
        |+from unittest import TestCase as TC, main
        |+import time
        |-import pandas
      """.stripMargin
    assert(extractPackages("Python", pythonImports).toSet === Set((1, "unittest"), (1, "time"), (-1, "pandas")))
  }

  test("Scala Packages") {
    // Scala
    val scalaImports =
      """
        |+      import org.apache.spark
        |       +import org.apache.spark.{SparkConf, SparkContext}
        |+import org.apache.spark.sql.SQLContext
        |-import       org.apache.spark.sql.SparkSession"""
    assert(extractPackages("Scala", scalaImports).toSet ===
      Set(
        (1, "org.apache.spark"),
        (1, "org.apache.spark.sql.SQLContext"),
        (-1, "org.apache.spark.sql.SparkSession")
      )
    )
  }

  test("Java Packages") {
    // Java
    val javaImports =
      """
        |+import static java.awt.Color;
        |+import java.awt.*;
        |-import javax.swing.JOptionPane;"""
    assert(extractPackages("Java", javaImports).toSet ===
      Set(
        (1, "java.awt.Color"),
        (1, "java.awt"),
        (-1, "javax.swing.JOptionPane")
      )
    )
  }

  test("Haskell Packages") {
    // Haskell
    val haskellImports =
      """
        |+import Mod1
        |+import Mod2 (x,y)
        |-import qualified Mod3
        |-import qualified Mod4
        |+import Mod5 hiding (x,y)
        |+import qualified Mod6"""
    assert(extractPackages("Haskell", haskellImports).toSet ===
      Set(
        (1, "Mod1"),
        (1, "Mod2"),
        (-1, "Mod3"),
        (-1, "Mod4"),
        (1, "Mod5"),
        (1, "Mod6")
      )
    )
  }

  test("Rust Packages") {
    val rustImports =
      """
        |+use TrafficLight::*;
        |+use APackage::{Red, Yellow};
      """.stripMargin
    assert(extractPackages("Rust", rustImports).toSet === Set((1, "TrafficLight"), (1, "APackage")))
  }

  test("JavaScript Imports") {
    val javaScriptImports =
      """
        |+import defaultExport from "module-name1";
        |+import * as name from "module-name2";
        |+import { export } from "module-name3";
        |+import { export as alias } from "module-name4";
        |+import { export1 , export2 } from "module-name5";
        |+import { export1 , export2 as alias2 , [...] } from "module-name6";
        |+import defaultExport, { export [ , [...] ] } from "module-name7";
        |+import defaultExport, * as name from "module-name8";
        |+import "module-name9";
      """.stripMargin
    assert(extractPackages("JavaScript", javaScriptImports).toSet === (for (n <- 1 to 9) yield (1, s"module-name$n")).toSet)
  }

  test("Kotlin Imports") {
    val kotlinImports =
      """
        |+import fooBar
        |+import foo.*
        |+import foo.Bar
        |+import bar.Bar as bBar
      """.stripMargin
    assert(extractPackages("Kotlin", kotlinImports).toSet === Set((1, "fooBar"), (1, "foo"), (1, "foo.Bar"), (1, "bar.Bar")))
  }

  test("C# Imports") {
    val cSharpImports =
      """
        |+using System.Text;
        |+using static System.Math;
        |+using Project = PC.MyCompany.Project;
      """.stripMargin

    assert(extractPackages("C#", cSharpImports).toSet === Set((1, "System.Text"), (1, "System.Math"), (1, "PC.MyCompany.Project")))
  }
  
  test("TypeScript Imports") {
    val typeScriptImports =
      """
        |+import { ZipCodeValidator as ZCV } from "./ZipCodeValidator";
        |+import { ZipCodeValidator as ZCV } from './lib/predicates/weak-map';
        |+import * as validator from './lib/predicates/base-predicate';
        |+import * as validator from './lib/under_score';
        |+import "./my-module.js";
        |+import PropTypes from 'prop-types'
      """.stripMargin
    assert(extractPackages("TypeScript", typeScriptImports).toSet ===
      Set(
        (1, "ZipCodeValidator"),
        (1, "lib/predicates/weak-map"),
        (1, "lib/predicates/base-predicate"),
        (1, "lib/under_score"),
        (1, "my-module.js"),
        (1, "prop-types")
      ))
  }
  
  test("F# Imports") {
    val fSharpImports =
      """
        |+open Fake
        |+open Fake.AssemblyInfoFile
        |+open Fake.Git
        |+open Fake.ReleaseNotesHelper
        |+open Fable
        |+open Fable.AST
      """.stripMargin
    
    assert(extractPackages("F#", fSharpImports).toSet ===
      Set(
        (1, "Fake"),
        (1, "Fake.AssemblyInfoFile"),
        (1, "Fake.Git"),
        (1, "Fake.ReleaseNotesHelper"),
        (1, "Fable"),
        (1, "Fable.AST")
      )
    )
  }
  
  test("Swift Imports") {
    val swiftImports =
      """
        |+import UIKit
        |+import UIKit.UITableViewController
        |+import class UIKit.UITableViewController
      """.stripMargin
    
    assert(extractPackages("Swift", swiftImports).toSet ===
      Set(
        (1, "UIKit"),
        (1, "UIKit.UITableViewController"),
        (1, "UIKit.UITableViewController")
      )
    )
  }
}

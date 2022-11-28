package playground

import chisel3.util.log2Up
import chisel3.{Bool, Bundle, Data, Input, Module, Mux, Output, Reg, UInt, fromIntToLiteral, fromIntToWidth}
import gemmini.{Arithmetic, Dataflow, PE, PEControl}
import runOption.ComplexRunner.generating

class Excercise1[T <: Data](inputType: T, outputType: T, accType: T, df: Dataflow.Value, max_simultaneous_matmuls: Int)
                   (implicit ev: Arithmetic[T]) extends Module {

  val io = IO(new Bundle {
    val in_a = Input(inputType)
    val in_b = Input(outputType)
    val in_d = Input(outputType)
    val out_b = Output(outputType)
    val out_c = Output(outputType)
    val out_ct = Output(UInt())
    val in_test1 = Input(UInt(4.W))
    val out_test1 = Output(UInt())
//    val out_test2 = Output(UInt())
  })

  import ev._

  val cType = if (df == Dataflow.WS) inputType else accType
  val a  = io.in_a
  val b  = io.in_b
  val d  = io.in_d
  val c1 = Reg(cType)
  val c2 = Reg(cType)

//  val fff = UInt(5.W)
//  val ggg= fff.getWidth
////  val shift = UInt(log2Up(fff.getWidth).W)
//
//  io.out_test2 := shift
  io.out_b := c2
  io.out_c := (c1 >> 5.U).clippedToWidthOf(outputType)
  io.out_ct := c1 >> 3.U
//  override def clippedToWidthOf(t: UInt) = {
//    val sat = ((1 << (t.getWidth-1))-1).U
//    Mux(self > sat, sat, self)(t.getWidth-1, 0)
//  }
  c2 := c2.mac(a, b.asTypeOf(inputType))
  c1 := d.withWidthOf(cType)
  io.out_test1 := (1 << io.in_test1.getWidth).U

}

object Excercise1 extends App{


  generating(new Excercise1(
    inputType = UInt(8.W),
    outputType = UInt(4.W),
    accType = UInt(5.W),
    df =  Dataflow.BOTH,
    max_simultaneous_matmuls = 3
  ), dir = "generated/gemmini")



}








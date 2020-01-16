package bergmann.masterarbeit.monitordsl.utils

import bergmann.masterarbeit.mappingdsl.mappingDSL.BinaryJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.DomainValue
import bergmann.masterarbeit.mappingdsl.mappingDSL.LengthUnitLiteral
import bergmann.masterarbeit.mappingdsl.mappingDSL.LiteralJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.TimeUnitLiteral
import bergmann.masterarbeit.mappingdsl.mappingDSL.UnaryJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.UnitBraces
import bergmann.masterarbeit.mappingdsl.mappingDSL.UnitDiv
import bergmann.masterarbeit.mappingdsl.mappingDSL.UnitExponent
import bergmann.masterarbeit.mappingdsl.mappingDSL.UnitLiteralAtom
import bergmann.masterarbeit.mappingdsl.mappingDSL.UnitMult
import bergmann.masterarbeit.monitordsl.monitorDSL.Add
import bergmann.masterarbeit.monitordsl.monitorDSL.AggregateExpression
import bergmann.masterarbeit.monitordsl.monitorDSL.CrossReference
import bergmann.masterarbeit.monitordsl.monitorDSL.Expression
import bergmann.masterarbeit.monitordsl.monitorDSL.FloatLiteral
import bergmann.masterarbeit.monitordsl.monitorDSL.IfThenElse
import bergmann.masterarbeit.monitordsl.monitorDSL.IntLiteral
import bergmann.masterarbeit.monitordsl.monitorDSL.MappingBinary
import bergmann.masterarbeit.monitordsl.monitorDSL.Mult
import bergmann.masterarbeit.monitordsl.monitorDSL.Negation
import bergmann.masterarbeit.monitordsl.monitorDSL.Subexpression
import bergmann.masterarbeit.monitordsl.monitorDSL.TimeOffset
import bergmann.masterarbeit.monitordsl.monitorDSL.UserVariable
import javax.measure.quantity.Quantity
import javax.measure.unit.NonSI
import javax.measure.unit.SI
import javax.measure.unit.Unit

import static extension bergmann.masterarbeit.monitordsl.utils.ExpressionTypeChecker.*
import bergmann.masterarbeit.monitordsl.monitorDSL.SquareRoot
import bergmann.masterarbeit.monitordsl.monitorDSL.Root
import bergmann.masterarbeit.monitordsl.monitorDSL.Absolute

class UnitUtils {

	def public static Unit getUnit(Expression expr){
		if(expr == null)
			return null
		if (!expr.isNumber)
			throw new IllegalArgumentException("Non-Number-Expressions don't have units: " + expr)
		switch expr{
			Add: {
				var lUnit = expr.left.unit
				var rUnit = expr.right.unit 
				if(lUnit == null || rUnit == null)
					return Unit.ONE
				return lUnit
			}
			Mult: {
				switch expr.op {
					case "*": return expr.left.unit.times(expr.right.unit)
					case "/": return expr.left.unit.divide(expr.right.unit)
				}
			}
			AggregateExpression: return expr.expr.unit
			IntLiteral: return if (expr.unit != null) expr.unit.toJavaUnit else Unit.ONE
			FloatLiteral: return if (expr.unit != null) expr.unit.toJavaUnit else Unit.ONE
			CrossReference: {
				var ref = expr.ref
				switch ref {
					UserVariable: return ref.expr.unit
					DomainValue: return ref.unit.toJavaUnit
					LiteralJava: return ref.unit.toJavaUnit
					UnaryJava: return ref.unit.toJavaUnit
					default:{
						System.err.println("UnitUtils: Unknown CrossRef" + ref.class)
						return Unit.ONE
					} 
				}
			}
			TimeOffset: return expr.expr.unit
			Subexpression: return expr.expr.unit
			Negation: return expr.expr.unit
			MappingBinary: return (expr.ref as BinaryJava).unit.toJavaUnit
			// MappingUnary: return (expr.ref as UnaryJava).unit.toJavaUnit 
			SquareRoot: return expr.expr.unit.root(2)
			Root: return expr.expr.unit.root(expr.root)
			Absolute: return expr.expr.unit
			IfThenElse: {
				var uThen = expr.then.unit
				var uElse = expr.getElse.unit
				if(uThen.isCompatible(uElse)){
					return uThen
				} else {
					throw new IllegalArgumentException("IfThenElse : Different units for then ("+uThen+ ") and else (" + uElse + ")")
				}
			}
			default: throw new IllegalArgumentException("Can't parse expr: " + expr)
		}
	}
	
	def public static boolean isUnitCompatible(Expression expr1, Expression expr2){
		if (!expr1.isNumber || !expr2.isNumber)
			throw new IllegalArgumentException("Non-Number-Expressions don't have units")
		else
			return expr1.unit.isCompatible(expr2.unit)
	}
	
	/**
 	* MonitorDSL Units
 	*/
	def public static Unit<? extends Quantity> toJavaUnit(bergmann.masterarbeit.monitordsl.monitorDSL.Unit unit){
		if (unit == null)
			return Unit.ONE
		switch unit{
			bergmann.masterarbeit.monitordsl.monitorDSL.UnitDiv: return unit.left.toJavaUnit.divide(unit.right.toJavaUnit)
			bergmann.masterarbeit.monitordsl.monitorDSL.UnitMult: return unit.left.toJavaUnit.times(unit.right.toJavaUnit)
			bergmann.masterarbeit.monitordsl.monitorDSL.UnitExponent:
				if(unit.isNegative)
					return unit.value.toJavaUnit.root(unit.exponent)
				else
					return unit.value.toJavaUnit.pow(unit.exponent)
			bergmann.masterarbeit.monitordsl.monitorDSL.UnitBraces: return unit.unit.toJavaUnit
			bergmann.masterarbeit.monitordsl.monitorDSL.UnitLiteralAtom: return unit.unit.toJavaUnit
			
			default: throw new IllegalArgumentException("Can't parse unit: " + unit)
		}
	}
	def public static Unit<? extends Quantity> toJavaUnit(bergmann.masterarbeit.monitordsl.monitorDSL.UnitLiteral literal){
		switch literal {
			bergmann.masterarbeit.monitordsl.monitorDSL.LengthUnitLiteral:
				switch literal.unit {
					case "mm": return SI.MILLI(SI.METER)
					case "cm": return SI.CENTI(SI.METER)
					case "m": return SI.METER
					case "km": return SI.KILO(SI.METER)
					default: throw new IllegalArgumentException("Can't parse unit: " + literal.unit)
				}
			bergmann.masterarbeit.monitordsl.monitorDSL.TimeUnitLiteral:
				switch literal.unit {
					case "ms": return SI.MILLI(SI.SECOND) 
					case "s": return SI.SECOND
					case "min": return NonSI.MINUTE
					case "h": return NonSI.HOUR
					default: throw new IllegalArgumentException("Can't parse unit: " + literal.unit)
				}
			default: throw new IllegalArgumentException("Can't parse unit: " + literal)
		}
	}
	
	/**
 	* MappingDSL Units
 	*/
	def public static Unit<? extends Quantity> toJavaUnit(bergmann.masterarbeit.mappingdsl.mappingDSL.Unit unit){
		if (unit == null)
			return Unit.ONE
		switch unit{
			UnitDiv: return unit.left.toJavaUnit.divide(unit.right.toJavaUnit)
			UnitMult: return unit.left.toJavaUnit.times(unit.right.toJavaUnit)
			UnitExponent:
				if(unit.isNegative)
					return unit.value.toJavaUnit.root(unit.exponent)
				else
					return unit.value.toJavaUnit.pow(unit.exponent)
			UnitBraces: return unit.unit.toJavaUnit
			UnitLiteralAtom: return unit.unit.toJavaUnit

			default: throw new IllegalArgumentException("Can't parse unit: " + unit)
		}
	}
	def public static Unit<? extends Quantity> toJavaUnit(bergmann.masterarbeit.mappingdsl.mappingDSL.UnitLiteral literal){
		switch literal {
			LengthUnitLiteral:
				switch literal.unit {
					case "mm": return SI.MILLI(SI.METER)
					case "cm": return SI.CENTI(SI.METER)
					case "m": return SI.METER
					case "km": return SI.KILO(SI.METER)
					default: throw new IllegalArgumentException("Can't parse unit: " + literal.unit)
				}
			TimeUnitLiteral:
				switch literal.unit {
					case "ms": return SI.MILLI(SI.SECOND) 
					case "s": return SI.SECOND
					case "min": return NonSI.MINUTE
					case "h": return NonSI.HOUR
					default: throw new IllegalArgumentException("Can't parse unit: " + literal.unit)
				}
			default: throw new IllegalArgumentException("Can't parse unit: " + literal)
		}
	}
	
	/* Unit compilation */
	// Convert to JScience Unit and use that
	// -- MonitorDSL Unit
	def static String toUnitString(bergmann.masterarbeit.monitordsl.monitorDSL.Unit unit){
		return unit.toJavaUnit.toUnitString
	}
	// -- MappingDSL Unit
	def static String toUnitString(bergmann.masterarbeit.mappingdsl.mappingDSL.Unit mappingUnit){
		return mappingUnit.toJavaUnit.toUnitString
	}
	// -- JScience Unit
	def static String toUnitString(javax.measure.unit.Unit u){
		if (u.equals(javax.measure.unit.Unit.ONE))
			return '''Unit.ONE'''
		else
		{
			var unitS =  u.toString
			unitS = unitS.replace("²","^2")
			unitS = unitS.replace("³","^3")
			return '''Unit.valueOf("«unitS»")'''		
		}
	}
}

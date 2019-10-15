package bergmann.masterarbeit.utils

import static extension bergmann.masterarbeit.utils.ExpressionTypeChecker.*
import bergmann.masterarbeit.monitorDsl.Expression
import javax.measure.quantity.*
import bergmann.masterarbeit.monitorDsl.*
import javax.measure.unit.Unit
import javax.measure.unit.SI
import javax.measure.unit.NonSI
import bergmann.masterarbeit.mappingdsl.mappingDSL.DomainValue
import bergmann.masterarbeit.mappingdsl.mappingDSL.LiteralJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.BinaryJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.UnaryJava

class UnitUtils {

	def public static Unit<? extends Quantity> getUnit(Expression expr){
		if (!expr.isNumber)
			throw new IllegalArgumentException("Non-Number-Expressions don't have units")
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
					default: return Unit.ONE
				}
			}
			TimeOffset: return expr.expr.unit
			MappingBinary: return (expr.ref as BinaryJava).unit.toJavaUnit // TODO: Allow JavaLiterals to have a unit
			MappingUnary: return (expr.ref as UnaryJava).unit.toJavaUnit  // TODO: Allow JavaLiterals to have a unit
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
	def public static Unit<? extends Quantity> toJavaUnit(bergmann.masterarbeit.monitorDsl.Unit unit){
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
	def public static Unit<? extends Quantity> toJavaUnit(UnitLiteral literal){
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
	
	/**
 	* MonitorDSL Units
 	*/
	def public static Unit<? extends Quantity> toJavaUnit(bergmann.masterarbeit.mappingdsl.mappingDSL.Unit unit){
		if (unit == null)
			return Unit.ONE
		switch unit{
			bergmann.masterarbeit.mappingdsl.mappingDSL.UnitDiv: return unit.left.toJavaUnit.divide(unit.right.toJavaUnit)
			bergmann.masterarbeit.mappingdsl.mappingDSL.UnitMult: return unit.left.toJavaUnit.times(unit.right.toJavaUnit)
			bergmann.masterarbeit.mappingdsl.mappingDSL.UnitExponent:
				if(unit.isNegative)
					return unit.value.toJavaUnit.root(unit.exponent)
				else
					return unit.value.toJavaUnit.pow(unit.exponent)
			bergmann.masterarbeit.mappingdsl.mappingDSL.UnitBraces: return unit.unit.toJavaUnit
			bergmann.masterarbeit.mappingdsl.mappingDSL.UnitLiteralAtom: return unit.unit.toJavaUnit

			default: throw new IllegalArgumentException("Can't parse unit: " + unit)
		}
	}
	def public static Unit<? extends Quantity> toJavaUnit(bergmann.masterarbeit.mappingdsl.mappingDSL.UnitLiteral literal){
		switch literal {
			bergmann.masterarbeit.mappingdsl.mappingDSL.LengthUnitLiteral:
				switch literal.unit {
					case "mm": return SI.MILLI(SI.METER)
					case "cm": return SI.CENTI(SI.METER)
					case "m": return SI.METER
					case "km": return SI.KILO(SI.METER)
					default: throw new IllegalArgumentException("Can't parse unit: " + literal.unit)
				}
			bergmann.masterarbeit.mappingdsl.mappingDSL.TimeUnitLiteral:
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
}
package bergmann.masterarbeit.utils

import bergmann.masterarbeit.monitorDsl.*
import java.util.ArrayList
import java.util.List

class UnitUtils {
	
	def public static double unitConversion(double value, Unit start, Unit target){
		return 1.2
	}
	
	def public static boolean compatible(Unit unitA, Unit unitB){
		if(unitA instanceof UnitBraces)
			return compatible(unitA.unit, unitB)
		if(unitB instanceof UnitBraces)
			return compatible(unitA, unitB.unit)
		if(unitA instanceof UnitLiteral){
			if(unitB instanceof UnitLiteral)
				// TODO Fix
				return true
			else
				return false
		}
		if(unitA instanceof UnitDiv){
			if(unitB instanceof UnitDiv)
				return compatible(unitA.left, unitB.left) && compatible(unitA.right, unitB.right)
			else
				return false
		}
		// TODO: Multiplication, Exponent
		return false
	}
	
	def private static List<Unit> toList(UnitExponent unit){
		var retVal = new ArrayList<Unit>()
		for(var i = 0; i<unit.exponent; i++){
			retVal.add(unit.value)
		}
		return retVal
	}
	
	def private static List<Unit> toList(UnitMult unit){
		var retVal = new ArrayList<Unit>()
		//TODO
		return retVal
	}
	
}
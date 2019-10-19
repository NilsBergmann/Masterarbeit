package bergmann.masterarbeit.utils

import bergmann.masterarbeit.monitorDsl.TimeAtom
import bergmann.masterarbeit.monitorDsl.TimeLiteral
import bergmann.masterarbeit.monitorDsl.InfinityTimeAtom
import bergmann.masterarbeit.monitorDsl.TimeIntervalSimple
import bergmann.masterarbeit.monitorDsl.TimeIntervalSingleton
import bergmann.masterarbeit.monitorDsl.TimeIntervalInequalityNotation
import bergmann.masterarbeit.monitorDsl.TimeInterval

class TimeUtils {
	
	def public static long toMillisec(int amount, String unit){
		switch unit {
			case "ms": return amount
			case "s" : return amount * 1000
			case "min": return amount * 1000 * 60
			case "h": return amount * 1000 * 60 * 60
			default: throw new Exception()
		}
	}
	def public static long toMillisec(TimeAtom atom){
		switch(atom){
			TimeLiteral: {
				var value = if (atom.neg) -1*atom.value else atom.value
				return toMillisec(value, atom.unit)
			}
			InfinityTimeAtom:{ 
				if(atom.neg)
					return Long.MIN_VALUE
				else 
					return Long.MAX_VALUE
			}
			default: throw new IllegalArgumentException("Can't parse TimeAtom " + atom)
			
		}
	}
	def public static boolean containsPositive(TimeInterval interval){
		switch(interval){
			TimeIntervalSimple: {
				return interval.start.containsPositive || interval.end.containsPositive
			}
			TimeIntervalSingleton:{ 
				return interval.value.containsPositive
			}
			TimeIntervalInequalityNotation:{
				return interval.value.containsPositive
			}
			default: throw new IllegalArgumentException("Can't parse TimeInterval " + interval)
		}
	}
	
	def public static boolean containsPositive(TimeAtom atom){
		switch(atom){
			TimeLiteral: {
				return !atom.neg
			}
			InfinityTimeAtom:{ 
				return !atom.neg
			}
			default: throw new IllegalArgumentException("Can't parse TimeAtom " + atom)
		}
	}
	
		def public static boolean containsNegative(TimeInterval interval){
		switch(interval){
			TimeIntervalSimple: {
				return interval.start.containsNegative || interval.end.containsNegative
			}
			TimeIntervalSingleton:{ 
				return interval.value.containsNegative
			}
			TimeIntervalInequalityNotation:{
				return interval.value.containsNegative
			}
			default: throw new IllegalArgumentException("Can't parse TimeInterval " + interval)
		}
	}
	
	def public static boolean containsNegative(TimeAtom atom){
		switch(atom){
			TimeLiteral: {
				return atom.neg
			}
			InfinityTimeAtom:{ 
				return atom.neg
			}
			default: throw new IllegalArgumentException("Can't parse TimeAtom " + atom)
		}
	}
	
	def public static boolean isZero(TimeInterval interval){
		switch(interval){
			TimeIntervalSimple: {
				return interval.start.isZero && interval.end.isZero
			}
			TimeIntervalSingleton:{ 
				return interval.value.isZero
			}
			TimeIntervalInequalityNotation:{
				return false
			}
			default: throw new IllegalArgumentException("Can't parse TimeInterval " + interval)
		}
	}
	
	def public static boolean isZero(TimeAtom atom){
		switch(atom){
			TimeLiteral: {
				return atom.value == 0
			}
			InfinityTimeAtom:{ 
				return false
			}
			default: throw new IllegalArgumentException("Can't parse TimeAtom " + atom)
		}
	}
	
}
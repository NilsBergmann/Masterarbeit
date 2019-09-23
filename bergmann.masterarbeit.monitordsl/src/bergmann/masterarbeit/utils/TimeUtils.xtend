package bergmann.masterarbeit.utils

import bergmann.masterarbeit.monitorDsl.TimeAtom
import bergmann.masterarbeit.monitorDsl.TimeLiteral
import bergmann.masterarbeit.monitorDsl.InfinityTimeAtom

class TimeUtils {
	def public static long toMillisec(TimeAtom atom){
		switch(atom){
			TimeLiteral: {
				switch (atom.unit){
					case "ms": return atom.value
					case "s" : return atom.value * 1000
					case "min": return atom.value * 1000 * 60
					case "h": return atom.value * 1000 * 60 * 60
					default: throw new Exception()
				}
			}
			InfinityTimeAtom: return Long.MAX_VALUE
		}
	}
}
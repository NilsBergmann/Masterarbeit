package bergmann.masterarbeit.utils

import java.util.List
import bergmann.masterarbeit.monitorDsl.*
import java.lang.reflect.Method
import java.util.ArrayList

class ExpressionUtils {
	def static List<Expression>getSubexpressions(Expression expr){
		// Dirty hack to get all subexpressions
		var subExps = new ArrayList<Expression>()
		for(Method m : expr.class.methods){
			if(m.name == "getExpr" || m.name =="getLeft" || m.name =="getRight"){
				var subExpr = m.invoke(expr) as Expression
				subExps.add(subExpr)
			}
		}
		return subExps	 
	}
}
package bergmann.masterarbeit.mappingdsl.utils

import bergmann.masterarbeit.mappingdsl.mappingDSL.Type
import bergmann.masterarbeit.mappingdsl.mappingDSL.JavaType
import bergmann.masterarbeit.mappingdsl.mappingDSL.BaseType
import bergmann.masterarbeit.mappingdsl.mappingDSL.BASE_VALUETYPE
import bergmann.masterarbeit.mappingdsl.mappingDSL.CustomJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.LiteralJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.UnaryJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.BinaryJava

class MappingUtils {
	static var BOOLEAN_JAVA_CLASS = "java.lang.Boolean"
	static var NUMBER_JAVA_CLASS = "org.jscience.physics.amount.Amount"
	static var STRING_JAVA_CLASS = "java.lang.String"
	static var ANY_JAVA_CLASS = "java.lang.Object"
	
	static var EXPRESSION_0 = "bergmann.masterarbeit.generationtarget.interfaces.Expression"
	static var EXPRESSION_1 = "bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression"
	static var EXPRESSION_2 = "bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression"
	def public static String toClassName(Type t){
		switch t{
			JavaType : return t.type.ref.javaType.qualifiedName
			BaseType: return t.type.toClassName
			default: return ""
		}
	}
	
	def static String toClassName(BASE_VALUETYPE t){
		switch t{
			case BOOLEAN: return BOOLEAN_JAVA_CLASS
			case NUMBER: return NUMBER_JAVA_CLASS
			case STRING: return STRING_JAVA_CLASS
			case ANY: return "T"
			default: throw new IllegalArgumentException("Can't parse type: " + t)
		}
	}
	
	def static String expectedQualifiedExpressionName(CustomJava cj){
		switch cj{
			LiteralJava: {
				var out = cj.type.toClassName
				return '''«EXPRESSION_0»<«out»>'''
			}
			UnaryJava: {
				var in = cj.type1.toClassName
				var out = cj.type2.toClassName
				return '''«EXPRESSION_1»<«in», «out»>'''
			}
			BinaryJava: {
				var in1 = cj.type1.toClassName
				var in2 = cj.type2.toClassName
				var out = cj.type3.toClassName
				return '''«EXPRESSION_2»<«in1», «in2», «out»>'''
			}
		}
	}
}
/*
 * generated by Xtext 2.12.0
 */
package bergmann.masterarbeit.generator

import bergmann.masterarbeit.monitorDsl.AGGREGATE_OPERATOR
import bergmann.masterarbeit.monitorDsl.Add
import bergmann.masterarbeit.monitorDsl.AggregateExpression
import bergmann.masterarbeit.monitorDsl.And
import bergmann.masterarbeit.monitorDsl.Assertion
import bergmann.masterarbeit.monitorDsl.BINARY_LTL_OPERATOR
import bergmann.masterarbeit.monitorDsl.BoolLiteral
import bergmann.masterarbeit.monitorDsl.Expression
import bergmann.masterarbeit.monitorDsl.FloatLiteral
import bergmann.masterarbeit.monitorDsl.Implication
import bergmann.masterarbeit.monitorDsl.IntLiteral
import bergmann.masterarbeit.monitorDsl.LTL_Binary
import bergmann.masterarbeit.monitorDsl.LTL_Unary
import bergmann.masterarbeit.monitorDsl.MappingBinary
import bergmann.masterarbeit.monitorDsl.MappingUnary
import bergmann.masterarbeit.monitorDsl.Monitors
import bergmann.masterarbeit.monitorDsl.Mult
import bergmann.masterarbeit.monitorDsl.Negation
import bergmann.masterarbeit.monitorDsl.Or
import bergmann.masterarbeit.monitorDsl.Rel
import bergmann.masterarbeit.monitorDsl.Subexpression
import bergmann.masterarbeit.monitorDsl.TimeInterval
import bergmann.masterarbeit.monitorDsl.TimeIntervalInequalityNotation
import bergmann.masterarbeit.monitorDsl.TimeIntervalSimple
import bergmann.masterarbeit.monitorDsl.TimeIntervalSingleton
import bergmann.masterarbeit.monitorDsl.UNARY_LTL_OPERATOR
import bergmann.masterarbeit.monitorDsl.Unit
import bergmann.masterarbeit.monitorDsl.UserVariable
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext

import static extension bergmann.masterarbeit.utils.ExpressionTypeChecker.*
import static extension bergmann.masterarbeit.utils.TimeUtils.*
import bergmann.masterarbeit.monitorDsl.CrossReference

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class MonitorDslGenerator extends AbstractGenerator {

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
//		fsa.generateFile('greetings.txt', 'People to greet: ' + 
//			resource.allContents
//				.filter(Greeting)
//				.map[name]
//				.join(', '))
		var monitors = resource.contents.head as Monitors
		fsa.generateFile("RunEvaluation.java", monitors.compile)		
	}
	
	def String compile(Monitors monitors){
		var assertions = monitors.assertions
		var userVars = monitors.uservars
		var databaseFilename = "test.db" //TODO Implement this
		//TODO: Add imports
		return '''
		class RunEvaluation {
			public static void main(String args[]) {
				DataController dataControl = new DataController();
				dataControl.connectToDatabase("�databaseFilename�");
				ArrayList<Assertion> assertions = new ArrayList<Assertion>();
				
				/**
				 * User Variables 
				 */
				�FOR userVar : userVars�
				�userVar.compile�
				
				�ENDFOR�
				
				/**
				 * Assertions
				 */
				 �FOR assertion : assertions�
				 �assertion.compile�
				 
				 �ENDFOR�
		}
		'''
	}
	
	def String compile(UserVariable userVar){
		var javaType = userVar.expr.expressionType
		if(javaType == null || javaType.equals(""))
			throw new IllegalArgumentException("UserVariable has invalid type " + javaType)
		return '''
		UserVariable<�javaType�> �userVar.name� = new UserVariable<�javaType�>(�userVar.expr.compile�);
		'''
	}
	
	def String compile(Assertion assertion){
		return '''
		Expression<Boolean> �assertion.name�_Expression = �assertion.expr.compile�;
		Assertion �assertion.name� = new Assertion(�assertion.name�_Expression, dataControl);
		assertions.add(�assertion.name�);
		'''
	}
	
	def String compile(Expression expr){
		switch expr{
			And: return '''new And(�expr.left.compile�,�expr.right.compile�)'''
			Or: return '''new Or(�expr.left.compile�,�expr.right.compile�)'''
			Implication: return '''new Implication(�expr.left.compile�,�expr.right.compile�)'''
			LTL_Unary: {
				if(expr.time == null)
					return '''new �expr.op.compile�(�expr.expr.compile�)'''
				else
					return  '''new �expr.op.compile�(�expr.expr.compile�, �expr.time.compile�)'''
			}
			LTL_Binary: {
				if(expr.time == null)
					return '''new �expr.op.compile�(�expr.left.compile�, �expr.right.compile�)'''
				else
					return '''new �expr.op.compile�(�expr.left.compile�, �expr.right.compile�, �expr.time.compile�)'''
			}
			Add: {
				if(expr.op == "+")
					return '''new Addition(�expr.left.compile�, �expr.right.compile�)'''
				else
					return '''new Subtraction(�expr.left.compile�, �expr.right.compile�)'''
			}
			Mult: {
				if(expr.op == "*")
					return '''new Multiplication(�expr.left.compile�, �expr.right.compile�)'''
				else
					return '''new Division(�expr.left.compile�, �expr.right.compile�)'''	
			}
			Negation: {
				if(expr.isBoolean)
					return '''new BoolNegation(�expr.expr.compile�)'''
				else if (expr.isNumber)
					return '''new NumberNegation(�expr.expr.compile�)'''
				else 
					throw new Exception()	
			
			}
			Rel:{
				if(expr.left.isBoolean && expr.right.isBoolean)
					return '''new BoolEquality(�expr.left.compile�, �expr.right.compile�, "�expr.op�")'''
				else if (expr.left.isNumber && expr.right.isNumber)
					return '''new NumberEquality(�expr.left.compile�, �expr.right.compile�, "�expr.op�")'''
				else 
					throw new Exception()
			}
			Subexpression: return expr.expr.compile
			IntLiteral: return '''new NumberLiteral(�expr.value�)''' //TODO Add handling of units
			FloatLiteral: return '''new NumberLiteral(�expr.value�)''' //TODO Add handling of units
			BoolLiteral: return '''new BoolLiteral(�expr.value�)'''
			AggregateExpression: return '''new �expr.op.compile�(�expr.expr.compile�, �expr.time.compile�)'''
			/* MappingDSL stuff */			
			CrossReference: return "TODO"
			MappingBinary: return compile(expr as MappingBinary)
			MappingUnary: compile(expr as MappingUnary)

			default:  throw new IllegalArgumentException("Can't parse expr: " + expr)
		}
	}
	
	def String compile(BINARY_LTL_OPERATOR op){
		switch op{
			case RELEASE: return "LTL_Release"
			case SINCE: return "PLTL_Since"
			case UNTIL: return "LTL_Until"
			case TRIGGER: return "PLTL_Trigger"
			case WEAK_UNTIL:  return "LTL_WeakUntil"
			default: throw new Exception()
		}
	}
	def String compile(UNARY_LTL_OPERATOR op){
		switch op{
			case NEXT: return "LTL_Next"
			case FINALLY: return "LTL_Finally"
			case GLOBAL: return "LTL_Global"
			case PREVIOUS: return "PLTL_Previous"
			case ONCE:  return "PLTL_Once"
			case Z: return "PLTL_Z"
			case HISTORICALLY : return "PLTL_Historically"
			default: throw new Exception()
		}
	}
	def String compile(AGGREGATE_OPERATOR op){
		switch op{
			case MIN: return "AggregateMinimum"
			case MAX: return "AggregateMaximum"
			case AVG: return "AggregateAverage"
			default: throw new Exception()
		}
	}
	

	
	def String compile(MappingBinary expr){
		return '''TODO_BinaryJava'''
	}
	def String compile(MappingUnary expr){
		return '''TODO_BinaryJava'''
	}
	
	def String compile(TimeInterval interval){
		switch interval{
			TimeIntervalSimple:{
				var includeLeft = interval.left.equals("[")
				var includeRight = interval.right.equals("]")
				var startMillisec = interval.start.toMillisec
				var endMillisec = interval.end.toMillisec
				return '''new RelativeTimeInterval(Duration.ofMillis(�startMillisec�9, Duration.ofMillis(�endMillisec�), �includeLeft�, �includeRight�)'''
			}
			TimeIntervalSingleton:{
				var timeString = '''Duration.ofMillis(�interval.value.toMillisec�)'''
				return '''new RelativeTimeInterval(�timeString�, �timeString�, true, true)'''
			}
			TimeIntervalInequalityNotation:{
				var timeString = '''Duration.ofMillis(�interval.value.toMillisec�)'''
				var zeroString = '''Duration.ofMillis(0)'''
				var infinityString = '''Duration.ofMillis(Long.MAX_VALUE)'''
				switch interval.op{
					case "<": return '''new RelativeTimeInterval(�zeroString�, �timeString�, true, false'''
					case "<=":return '''new RelativeTimeInterval(�zeroString�, �timeString�, true, true'''
					case ">": return '''new RelativeTimeInterval(�timeString�, �infinityString�, false, true'''
					case ">=":return '''new RelativeTimeInterval(�timeString�, �infinityString�, true, true'''
					default: throw new Exception()
				}
			}
			default: throw new Exception()
		}
	}
	
	def String compile(Unit unit){
		return "TODO_UNIT"
	}
}

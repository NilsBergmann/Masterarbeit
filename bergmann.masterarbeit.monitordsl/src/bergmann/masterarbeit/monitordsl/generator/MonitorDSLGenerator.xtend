/*
 * generated by Xtext 2.19.0
 */
package bergmann.masterarbeit.monitordsl.generator

import bergmann.masterarbeit.mappingdsl.mappingDSL.BinaryJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.DomainValue
import bergmann.masterarbeit.mappingdsl.mappingDSL.JavaClassReference
import bergmann.masterarbeit.mappingdsl.mappingDSL.LiteralJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.UnaryJava
import bergmann.masterarbeit.monitordsl.monitorDSL.AGGREGATE_OPERATOR
import bergmann.masterarbeit.monitordsl.monitorDSL.Add
import bergmann.masterarbeit.monitordsl.monitorDSL.AggregateExpression
import bergmann.masterarbeit.monitordsl.monitorDSL.And
import bergmann.masterarbeit.monitordsl.monitorDSL.Assertion
import bergmann.masterarbeit.monitordsl.monitorDSL.BINARY_LTL_OPERATOR
import bergmann.masterarbeit.monitordsl.monitorDSL.BoolLiteral
import bergmann.masterarbeit.monitordsl.monitorDSL.CrossReference
import bergmann.masterarbeit.monitordsl.monitorDSL.Expression
import bergmann.masterarbeit.monitordsl.monitorDSL.FloatLiteral
import bergmann.masterarbeit.monitordsl.monitorDSL.IfThenElse
import bergmann.masterarbeit.monitordsl.monitorDSL.Implication
import bergmann.masterarbeit.monitordsl.monitorDSL.IntLiteral
import bergmann.masterarbeit.monitordsl.monitorDSL.LTL_Binary
import bergmann.masterarbeit.monitordsl.monitorDSL.LTL_Unary
import bergmann.masterarbeit.monitordsl.monitorDSL.MappingBinary
import bergmann.masterarbeit.monitordsl.monitorDSL.Monitors
import bergmann.masterarbeit.monitordsl.monitorDSL.Mult
import bergmann.masterarbeit.monitordsl.monitorDSL.Negation
import bergmann.masterarbeit.monitordsl.monitorDSL.Or
import bergmann.masterarbeit.monitordsl.monitorDSL.Rel
import bergmann.masterarbeit.monitordsl.monitorDSL.StateOffset
import bergmann.masterarbeit.monitordsl.monitorDSL.StringLiteral
import bergmann.masterarbeit.monitordsl.monitorDSL.Subexpression
import bergmann.masterarbeit.monitordsl.monitorDSL.TimeInterval
import bergmann.masterarbeit.monitordsl.monitorDSL.TimeIntervalInequalityNotation
import bergmann.masterarbeit.monitordsl.monitorDSL.TimeIntervalSimple
import bergmann.masterarbeit.monitordsl.monitorDSL.TimeIntervalSingleton
import bergmann.masterarbeit.monitordsl.monitorDSL.TimeLiteral
import bergmann.masterarbeit.monitordsl.monitorDSL.TimeOffset
import bergmann.masterarbeit.monitordsl.monitorDSL.UNARY_LTL_OPERATOR
import bergmann.masterarbeit.monitordsl.monitorDSL.UserVariable
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext

import static extension bergmann.masterarbeit.monitordsl.utils.ExpressionTypeChecker.*
import static extension bergmann.masterarbeit.monitordsl.utils.ExpressionUtils.*
import static extension bergmann.masterarbeit.monitordsl.utils.ImportUtils.*
import static extension bergmann.masterarbeit.monitordsl.utils.TimeUtils.*
import static extension bergmann.masterarbeit.monitordsl.utils.UnitUtils.*

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class MonitorDSLGenerator extends AbstractGenerator {

override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
//		fsa.generateFile('greetings.txt', 'People to greet: ' + 
//			resource.allContents
//				.filter(Greeting)
//				.map[name]
//				.join(', '))
		var monitors = resource.contents.head as Monitors
		fsa.generateFile("bergmann/masterarbeit/generated/"+monitors.targetClassname + "_StandaloneRunner.java", monitors.createStandaloneRunner)		
		fsa.generateFile("bergmann/masterarbeit/generated/"+monitors.targetClassname + "_MonitorDeclaration.java", monitors.createEvaluationPackage)		
	}
	
	def static String getTargetClassname(Monitors monitors){
		return monitors.package.name
	}
	
	def String createEvaluationPackage(Monitors monitors){
		var assertions = monitors.recursiveAssertions
		var userVars = monitors.recursiveUserVariables
		return'''
		�monitors.compilePackage�
		
		/* Imports */
		�monitors.compileImports�
		/* Imports end */
		
		/**
		�FOR mon : monitors.recursiveImportedMonitors�
		* Includes monitors �mon.getTargetClassname�
		�ENDFOR�
		�FOR dom : monitors.recursiveImportedDomains�
		* Includes domain �dom.package.name�
		�ENDFOR�
		*/
		
		@SuppressWarnings("unused")
		public class �monitors.targetClassname�_MonitorDeclaration extends MonitorDeclaration {
			@SuppressWarnings({"rawtypes", "unchecked"})
			public �monitors.targetClassname�_MonitorDeclaration(){
				super();
				
				this.setName("�monitors.package.name�");
				
				/**
				* Required Data
				*/
				
				�monitors.registerDomainColumns�
				
				/**
				* Init Assertions and UserVariables
				*/
				
				�FOR userVar : userVars�
				UserVariable<�userVar.expr.expressionType�> �userVar.name� = new UserVariable<�userVar.expr.expressionType�>("�userVar.name�");
				�ENDFOR�
				
				�FOR assertion : assertions�
				Assertion �assertion.name� = new Assertion("�assertion.name�");
				�ENDFOR�
				
				/**
				* Set UserVariable expressions
				*/
				
				�FOR userVar : userVars�
				�userVar.compile�
				
				�ENDFOR�
				
				/**
				* Set Assertion expressions
				*/
				
				�FOR assertion : assertions�
				�assertion.compile�
				
				�ENDFOR�
			}
		}
		'''
	}
	def String createStandaloneRunner(Monitors monitors){
		var assertions = monitors.recursiveAssertions
		var userVars = monitors.recursiveUserVariables
		return '''
		�monitors.compilePackage�
		�monitors.compileImports�
		@SuppressWarnings("unused")
		public class �monitors.targetClassname�_StandaloneRunner {
			
			@SuppressWarnings("rawtypes")
			public static void main(String args[]) {
				�generateSetup�
				
				MonitorDeclaration monitors = new �monitors.targetClassname�_MonitorDeclaration();
				dataControl.runEvaluation(monitors,tableSelection); 
			}
		
		}
		'''
	}
	
	def static String registerDomainColumns(Monitors monitors){
		var s = ""
		var domains = monitors.recursiveImportedDomains
		for (currentDomain : domains) {
			s+= "// Domain: " + currentDomain.package.name + "\n"
			for (dv : EcoreUtil2.eAllOfType(currentDomain, DomainValue)) {
				s+= if(dv.description != null) "// " +dv.description +"\n" else "// No Description given.\n"
				s+= dv.registerDomainColumn + "\n"
			}  
			s+= "\n"
		}
		return s
	}
	
	def static String registerDomainColumn(DomainValue dv){
		switch dv.type {
			case BOOLEAN: return '''this.getRequiredDataBooleans().add("�dv.column�");'''
			case NUMBER: return  '''this.getRequiredDataNumbers().put("�dv.column�", �dv.unit.toUnitString�);'''
			case STRING: return '''this.getRequiredDataStrings().add("�dv.column�");'''
			default:  throw new IllegalArgumentException("Can't parse type: " + dv.type)
		}
	}
	def static String generateSetup(){
				'''
		/**
		* Setup
		*/
		// -- Choose real time mode
		int realtimeSelection = UIUtils.isRealTimeSelection();
		if (realtimeSelection == 2) {
		    // User selected "Cancel"
			UIUtils.showError("User selected cancel");
			System.exit(0);
		}
		boolean isRealTime = realtimeSelection == 0;
		
		// -- Choose database
		File dbFile = UIUtils.databaseSelection();
		if (dbFile == null) {
			UIUtils.showError("Please select a database file.");
			System.exit(1);
		}
		System.out.println("Selected " + dbFile.getAbsolutePath());
		
		// -- Try Database
		StandaloneDataController dataControl = new StandaloneDataController(isRealTime);
		dataControl.connectToDatabase(dbFile.getAbsolutePath());
		if (!dataControl.isConnectedToDB()) {
			UIUtils.showError("Could not open database " + dbFile.getAbsolutePath());
			System.exit(1);
		}
		
		// -- Select Table
		List<String> tables = dataControl.getTables();
		if (tables.size() < 1) {
				UIUtils.showError("No tables available");
				System.exit(1);
		}
		String tableSelection = UIUtils.selectTable(tables);
		if (tableSelection == null || tableSelection.equals("")) {
			UIUtils.showError("No table selected");
			System.exit(1);
		}
		dataControl.selectTable(tableSelection);
		'''
	}
	def static String compilePackage(Monitors monitors){
		return '''package bergmann.masterarbeit.generated;'''; //TODO: PackageName
	}
	
	def static String compileImports(Monitors monitors){
		'''
		import java.io.File;
		import java.time.Duration;
		import java.time.Instant;
		import java.util.ArrayList;
		import java.util.List;
		
		import javax.measure.unit.Unit;
		
		import org.jscience.physics.amount.Amount;
		
		import bergmann.masterarbeit.generationtarget.dataaccess.*;
		import bergmann.masterarbeit.generationtarget.expressions.*;
		import bergmann.masterarbeit.generationtarget.interfaces.*;
		import bergmann.masterarbeit.generationtarget.utils.*;
		'''
	}
	
	def String compile(UserVariable userVar){
		var javaType = userVar.expr.expressionType
		if(javaType == null || javaType.equals(""))
			throw new IllegalArgumentException("UserVariable has invalid type " + javaType)
		return '''
		�userVar.name�.setExpression(�userVar.expr.compile�);
		this.getUserVarExpressions().add(�userVar.name�); 
		'''
	}
	
	def String compile(Assertion assertion){
		return '''
		�assertion.name�.setExpression(�assertion.expr.compile�);
		this.getAssertionExpressions().add(�assertion.name�); 
		''' 
	}
	
	def String positiveHash(Object obj){
		return "" + (obj.hashCode.bitwiseAnd(0xfffffff)) // Removes -
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
				if(expr.op.equals("+"))
					return '''new Addition(�expr.left.compile�, �expr.right.compile�)'''
				else
					return '''new Subtraction(�expr.left.compile�, �expr.right.compile�)'''
			}
			Mult: {
				if(expr.op.equals("*"))
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
				if(expr.op.equals("=="))
					return '''new Equals<�expr.left.expressionType�, �expr.right.expressionType�>(�expr.left.compile�, �expr.right.compile�)'''
				if(expr.op.equals("!="))
					return '''new NotEquals<�expr.left.expressionType�, �expr.right.expressionType�>(�expr.left.compile�, �expr.right.compile�)'''
				else if (expr.left.isNumber && expr.right.isNumber)
					return '''new NumberInequality(�expr.left.compile�, �expr.right.compile�, "�expr.op�")'''
				else 
					throw new Exception()
			}
			Subexpression: return expr.expr.compile
			IntLiteral: return '''new NumberLiteral(�expr.value�, �expr.unit.toUnitString�)''' //TODO Add handling of units
			FloatLiteral: return '''new NumberLiteral(�expr.value�, �expr.unit.toUnitString�)''' //TODO Add handling of units
			BoolLiteral: return '''new BoolLiteral(�expr.value�)'''
			StringLiteral: return '''new StringLiteral("�expr.value�")'''
			AggregateExpression: return '''new �expr.op.compile�(�expr.expr.compile�, �expr.time.compile�)'''
			IfThenElse: return '''new IfThenElse<�expr.then.expressionType�>(�expr.condition.compile�, �expr.then.compile�, �expr.getElse.compile�)'''
			TimeOffset: return compile(expr as TimeOffset)
			
			/* MappingDSL stuff */			
			CrossReference: {
				var ref = expr.ref
				switch ref{
					UserVariable: return ref.name
					DomainValue:{
							switch ref.type {
							case BOOLEAN: return '''new BooleanDatabaseAccess("�ref.column�")'''
							case NUMBER: return '''new NumberDatabaseAccess("�ref.column�")'''
							case STRING: return '''new StringDatabaseAccess("�ref.column�")'''
							default: throw new IllegalArgumentException("Can't parse DomainValue: " + ref + " with type " + ref.type)
						 }
						}
					LiteralJava: return '''new �ref.ref.className�()'''
					UnaryJava:{
						var refMapping = expr.ref as UnaryJava
						return '''new �refMapping.ref.className�(�expr.optionalExpr.compile�)'''
					}
					default: throw new IllegalArgumentException("Can't parse expr: " + expr + " referencing " + ref)
				}
			}
			MappingBinary: return compile(expr as MappingBinary)
			default:  throw new IllegalArgumentException("Can't parse expr: " + expr)
		}
	}
	
	def static String getClassName(JavaClassReference ref){
		return ref.javaType.qualifiedName
	}
	def String compile(BINARY_LTL_OPERATOR op){
		switch op{
			case RELEASE: return "LTL_Release"
			case SINCE: return "PLTL_Since"
			case UNTIL: return "LTL_Until"
			case TRIGGER: return "PLTL_Trigger"
			case WEAK_UNTIL:  return "LTL_WeakUntil"
			default: throw new IllegalArgumentException("Unknown operator: " + op)
		}
	}
	def String compile(UNARY_LTL_OPERATOR op){
		switch op{
			case NEXT: return "LTL_Next"
			case FINALLY: return "LTL_Finally"
			case GLOBAL: return "LTL_Global"
			case YESTERDAY: return "PLTL_Yesterday"
			case ONCE:  return "PLTL_Once"
			case Z: return "PLTL_Z"
			case HISTORICALLY : return "PLTL_Historically"
			default: throw new IllegalArgumentException("Unknown operator: " + op)
		}
	}
	def String compile(AGGREGATE_OPERATOR op){
		switch op{
			case MIN: return "AggregateMinimum"
			case MAX: return "AggregateMaximum"
			case AVG: return "AggregateAverage"
			default: throw new IllegalArgumentException("Unknown operator: " + op)
		}
	}
	
	
	def String compile(MappingBinary expr){
		var refMapping = expr.ref as BinaryJava
		return '''new �refMapping.ref.className�(�expr.left.compile�, �expr.right.compile�)'''
	}

	
	def String compile(TimeInterval interval){
		switch interval{
			TimeIntervalSimple:{
				var includeLeft = interval.left.equals("[")
				var includeRight = interval.right.equals("]")
				var startMillisec = if (interval.start instanceof TimeLiteral) interval.start.toMillisec else "Long.MIN_VALUE"
				var endMillisec = if (interval.end instanceof TimeLiteral) interval.end.toMillisec else "Long.MAX_VALUE"
				return '''new RelativeTimeInterval(Duration.ofMillis(�startMillisec�), Duration.ofMillis(�endMillisec�), �includeLeft�, �includeRight�)'''
			}
			TimeIntervalSingleton:{
				var timeString = '''Duration.ofMillis(�interval.value.toMillisec�)'''
				return '''new RelativeTimeInterval(�timeString�, �timeString�, true, true)'''
			}
			TimeIntervalInequalityNotation:{
				var timeString = '''Duration.ofMillis(�interval.value.toMillisec�)'''
				var zeroString = '''Duration.ofMillis(0)'''
				var infinityString = if(interval.containsPositive) '''Duration.ofMillis(Long.MAX_VALUE)''' else '''Duration.ofMillis(Long.MIN_VALUE)'''
				switch interval.op{
					case "<": return '''new RelativeTimeInterval(�zeroString�, �timeString�, true, false)'''
					case "<=":return '''new RelativeTimeInterval(�zeroString�, �timeString�, true, true)'''
					case ">": return '''new RelativeTimeInterval(�timeString�, �infinityString�, false, true)'''
					case ">=":return '''new RelativeTimeInterval(�timeString�, �infinityString�, true, true)'''
					default: throw new IllegalArgumentException("Unknown operator: " + interval.op)
				}
			}
			default: throw new IllegalArgumentException("Unknown interval: " + interval)
		}
	}
	
	def String compile(TimeOffset expr){
		var offset = expr.offset
		switch offset{
			StateOffset: {
				var amount = if(offset.op == "+") offset.value else -offset.value
				return '''new OffsetByStates<�expr.expr.expressionType�>(�expr.expr.compile�, �amount�)'''
				}
			TimeOffset:{
				var amount = if(offset.op == "+") offset.value else -offset.value
				var millis = toMillisec(amount, offset.unit)
				var durationString = '''Duration.ofMillis(�millis�)'''
				return '''new OffsetByTime<�expr.expr.expressionType�>(�expr.expr.compile�, �durationString�)'''
			}
			default: throw new IllegalArgumentException("Unknown Offset: " + offset)
		}
	}
	
}

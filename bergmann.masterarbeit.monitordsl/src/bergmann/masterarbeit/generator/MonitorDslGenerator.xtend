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
import static extension bergmann.masterarbeit.utils.UnitUtils.*
import static extension bergmann.masterarbeit.utils.ExpressionUtils.*
import bergmann.masterarbeit.monitorDsl.CrossReference
import bergmann.masterarbeit.mappingdsl.mappingDSL.DomainValue
import bergmann.masterarbeit.mappingdsl.mappingDSL.JavaClassReference
import bergmann.masterarbeit.mappingdsl.mappingDSL.LiteralJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.BinaryJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.UnaryJava
import javax.measure.quantity.Quantity
import bergmann.masterarbeit.monitorDsl.StringLiteral
import bergmann.masterarbeit.monitorDsl.TimeLiteral
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.EcoreUtil2
import bergmann.masterarbeit.monitorDsl.TimeOffset
import bergmann.masterarbeit.monitorDsl.StateOffset
import bergmann.masterarbeit.monitorDsl.IfThenElse

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
		fsa.generateFile(monitors.targetClassname + "_StandaloneRunner.java", monitors.createStandaloneRunner)		
		fsa.generateFile(monitors.targetClassname + "_MonitorDeclaration.java", monitors.createEvaluationPackage)		
	}
	
	def static String getTargetClassname(Monitors monitors){
		return monitors.package.name
	}
	
	def String createEvaluationPackage(Monitors monitors){
		var assertions = monitors.assertions
		var userVars = monitors.uservars
		return'''
		�monitors.compilePackage�
		�monitors.compileImports�
		
		@SuppressWarnings("unused")
		class �monitors.targetClassname�_MonitorDeclaration extends EvaluationPackage {
			
			@SuppressWarnings("rawtypes")
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
				UserVariable �userVar.name�_�userVar.positiveHash� = new UserVariable<�userVar.expr.expressionType�>("�userVar.name�");
				�ENDFOR�
				
				�FOR assertion : assertions�
				Assertion �assertion.name�_�assertion.positiveHash� = new Assertion("�assertion.name�");
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
		var assertions = monitors.assertions
		var userVars = monitors.uservars
		return '''
		�monitors.compilePackage�
		�monitors.compileImports�
		@SuppressWarnings("unused")
		class �monitors.targetClassname�_StandaloneRunner {
			
			@SuppressWarnings("rawtypes")
			public static void main(String args[]) {
				�generateSetup�
				//TODO
			}
		
		}
		'''
	}
	
	def static String registerDomainColumns(Monitors monitors){
		var s = ""
		var domains = monitors.importedDomains
		for (currentDomain : domains) {
			s+= "// Domain: " + currentDomain.package.name + "\n"
			for (dv : EcoreUtil2.eAllOfType(currentDomain, DomainValue)) {
				s+= dv.registerDomainColumn + "\n"
			}  
			s+= "\n"
		}
		return s
	}
	
	def static String registerDomainColumn(DomainValue dv){
		switch dv.type {
			case BOOLEAN: return '''this.getRequiredDataBooleans().add("�dv.column�");'''
			case NUMBER: return  '''this.getRequiredDataNumbers().put("�dv.column�", �dv.unit.compile�);'''
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
		DataController dataControl = new DataController(isRealTime);
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
		return ''''''; //TODO: PackageName
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
		�userVar.name�_�userVar.positiveHash�.setExpression(�userVar.expr.compile�);
		this.getUserVarExpressions().add(�userVar.name�_�userVar.positiveHash�); 
		'''
	}
	
	def String compile(Assertion assertion){
		return '''
		Assertion �assertion.name�_�assertion.positiveHash�.setExpression("�assertion.name�", �assertion.expr.compile�);
		this.getAssertionExpressions().add(�assertion.name�_�assertion.positiveHash�); 
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
			IntLiteral: return '''new NumberLiteral(�expr.value�, �expr.unit.compile�)''' //TODO Add handling of units
			FloatLiteral: return '''new NumberLiteral(�expr.value�, �expr.unit.compile�)''' //TODO Add handling of units
			BoolLiteral: return '''new BoolLiteral(�expr.value�)'''
			StringLiteral: return '''new StringLiteral("�expr.value�")'''
			AggregateExpression: return '''new �expr.op.compile�(�expr.expr.compile�, �expr.time.compile�)'''
			IfThenElse: return '''new IfThenElse<�expr.then.expressionType�>(�expr.condition.compile�, �expr.then.compile�, �expr.getElse.compile�)'''
			TimeOffset: return compile(expr as TimeOffset)
			
			/* MappingDSL stuff */			
			CrossReference: {
				var ref = expr.ref
				switch ref{
					UserVariable: return ref.name+"_"+ref.positiveHash
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
	
	/* Unit compilation */
	// Convert to JScience Unit and use that
	// -- MonitorDSL Unit
	def static String compile(Unit unit){
		return unit.toJavaUnit.toJavaString
	}
	// -- MappingDSL Unit
	def static String compile(bergmann.masterarbeit.mappingdsl.mappingDSL.Unit mappingUnit){
		return mappingUnit.toJavaUnit.toJavaString
	}
	// -- JScience Unit
	def static String toJavaString(javax.measure.unit.Unit<? extends Quantity> u){
		if (u.equals(javax.measure.unit.Unit.ONE))
			return '''Unit.ONE'''
		else
			return '''Unit.valueOf("�u.toString�")'''
	}
}

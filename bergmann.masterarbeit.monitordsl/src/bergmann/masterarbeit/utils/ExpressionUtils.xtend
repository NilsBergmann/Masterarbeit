package bergmann.masterarbeit.utils

import java.util.List
import bergmann.masterarbeit.monitorDsl.*
import java.lang.reflect.Method
import java.util.ArrayList
import bergmann.masterarbeit.mappingdsl.mappingDSL.Domain
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.IPath
import org.eclipse.emf.ecore.plugin.EcorePlugin
import bergmann.masterarbeit.mappingdsl.mappingDSL.impl.PackageDeclarationImpl

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
	
	def static List<UserVariable> getAllUserVariables(Monitors main){
		var userVars = new ArrayList(main.uservars)
		return userVars
	}
	
	def static List<Domain> getImportedDomains(Monitors main){
		var references = new ArrayList<Domain>()
		for(Import current : main.imports){
			var referencedRoot = EcoreUtil.getRootContainer(current.ref)
			if(referencedRoot instanceof Domain)
				references.add(referencedRoot)
		}
		return references
	}
	
	def static fileExists(EObject context, String relativeFilePath){
		return false
		//return context.resource.project.findMember(relativeFilePath) != null
	}
}
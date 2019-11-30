package bergmann.masterarbeit.monitordsl.utils

import bergmann.masterarbeit.mappingdsl.mappingDSL.Domain
import bergmann.masterarbeit.mappingdsl.mappingDSL.NamedDomainElement
import bergmann.masterarbeit.monitordsl.monitorDSL.Assertion
import bergmann.masterarbeit.monitordsl.monitorDSL.ImportDomain
import bergmann.masterarbeit.monitordsl.monitorDSL.ImportMonitor
import bergmann.masterarbeit.monitordsl.monitorDSL.Monitors
import bergmann.masterarbeit.monitordsl.monitorDSL.UserVariable
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.EcoreUtil2
import java.util.HashSet
import java.util.Set

class ImportUtils {
	def static List<Assertion> recursiveAssertions(Monitors main) {
		var assertions = new ArrayList(main.assertions)
		for (Monitors current : main.recursiveImportedMonitors) {
			assertions.addAll(current.assertions)
		}
		return assertions
	}

	def static List<UserVariable> recursiveUserVariables(Monitors main) {
		var userVars = new ArrayList(main.uservars)
		for (Monitors current : main.recursiveImportedMonitors) {
			userVars.addAll(current.uservars)
		}
		return userVars
	}

	def static List<Monitors> recursiveImportedMonitors(Monitors main) {
		var ret =  recursionHelperImportedMonitors(main,new HashSet<Monitors>()).toList
		ret.remove(main)
		// System.err.println(main.package.name + "->" + ret.map[Monitors m | m.package.name])
		return ret
	}
	def private static Set<Monitors> recursionHelperImportedMonitors(Monitors current, Set<Monitors> seen){
		if(seen.contains(current))
			return seen
		else {
			seen.add(current)
			for (imported : current.nonRecursiveImportedMonitors) {
				seen.addAll(recursionHelperImportedMonitors(imported, seen))
			}
			return seen
		}
	}

	def static List<Monitors> nonRecursiveImportedMonitors(Monitors main) {
		var references = new HashSet<Monitors>()
		for (ImportMonitor current : main.monitorImports) {
			var referencedRoot = EcoreUtil.getRootContainer(current.ref)
			if (referencedRoot instanceof Monitors && !referencedRoot.equals(main))
				references.add(referencedRoot as Monitors)
		}
		return references.toList
	}

	def static List<Domain> nonRecursiveImportedDomains(Monitors main) {
		var references = new HashSet<Domain>()
		for (ImportDomain current : main.domainImports) {
			var referencedRoot = EcoreUtil.getRootContainer(current.ref)
			if (referencedRoot instanceof Domain)
				references.add(referencedRoot)
		}
		return references.toList
	}

	def static List<Domain> recursiveImportedDomains(Monitors main) {
		var references = main.nonRecursiveImportedDomains
		for (referenced : main.recursiveImportedMonitors) {
			references.addAll(referenced.nonRecursiveImportedDomains)
		}
		return references.toList
	}

	def static HashMap<EObject, String> getAllNamedObjectsRecursive(Monitors main) {
		var names = new HashMap<EObject, String>()
		for (current : main.recursiveImportedMonitors) {
			names.putAll(current.allNamedObjects)
		}
		for (current : main.recursiveImportedDomains) {
			names.putAll(current.allNamedObjects)
		}
		names.putAll(main.allNamedObjects)
		return names
	}

	def static HashMap<EObject, String> getAllNamedObjects(Monitors main) {
		var names = new HashMap<EObject, String>()
		for (current : main.assertions) {
			names.put(current, current.name)
		}
		for (current : main.uservars) {
			names.put(current, current.name)
		}
		return names
	}

	def static HashMap<NamedDomainElement, String> getAllNamedObjects(Domain main) {
		var ret = new HashMap<NamedDomainElement, String>()
		for (current : EcoreUtil2.eAllOfType(main, NamedDomainElement)) {
			ret.put(current, current.name)
		}
		return ret
	}
}

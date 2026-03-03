import phpTypes.DocTag;
import phpTypes.PhpDocBlock;
import phpTypes.PhpType;
import phpTypes.PhpVariable;

import java.util.List;

public class TypeResolver {
    public static PhpType inferTypeFromDoc(PhpVariable variable) {
        PhpDocBlock docBlock = variable.getDocBlock();

        // Fallback -  If no DocBlock exists
        if (docBlock == null) {
            return TypeFactory.createType("mixed");
        }

        List<DocTag> varTags = docBlock.getTagsByName("var");
        // Fallback - if no matching tag is found
        if (varTags == null || varTags.isEmpty()) {
            return TypeFactory.createType("mixed");
        }

        for (DocTag varTag : varTags) {
            String value = varTag.getValue();
            // Skip empty or whitespace-only values
            if (value == null || value.trim().isEmpty()) {
                continue;
            }

            // This regex splits the values (we got rid of @var earlier) by whitespaces
            // (e.g., "int $x Description" -> ["int", "$x", "Description"])
            String[] parts = value.trim().split("\\s+");
            String varType = parts[0]; // The type is the first part - our type

            // Here is the case where we encounter Named Tag (e.g., "@var Logger $log")
            if (parts.length > 1 && parts[1].startsWith("@")) {
                // Check if we have name mismatch
                // (e.g., "@var Admin $adm" for variable $guest)
                if (!parts[1].equals(variable.getName())) {
                    continue; // Skip if the variable name doesn't match
                }
            }

            // Check if we have union types
            // and create a UnionType if we do (e.g., "int|string")
            if (varType.contains("|")) {
                String[] unionTypes = varType.split("\\|");
                List<PhpType> phpTypes = java.util.Arrays.stream(unionTypes)
                        .map(String::trim)
                        .filter(type -> !type.isEmpty())
                        .map(TypeFactory::createType)
                        .toList();
                return TypeFactory.createUnionType(phpTypes);
            }

            // Return the inferred type
            return TypeFactory.createType(varType);
        }

        // Default fallback if no valid type is found
        return TypeFactory.createType("mixed");
    }
}

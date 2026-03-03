import phpTypes.PhpType;
import phpTypes.UnionType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This interface provides factory methods to create PhpType and UnionType instances.
 */
public interface TypeFactory {
    /** Converts a string to a PhpType */
    static PhpType createType(String typeName) {
        return new PhpType() {
            @Override
            public String toString() {
                return typeName;
            }
        };
    }

    /** Combines multiple PhpType objects into a UnionType */
    static UnionType createUnionType(List<PhpType> types) {
        return new UnionType() {
            @Override
            public String toString() {
                return types.stream()
                        .map(PhpType::toString)
                        .collect(Collectors.joining("|"));
            }
        };
    }
}

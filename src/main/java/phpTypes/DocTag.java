package phpTypes;

/**
 * This interface represents a documentation tag in a PHPDoc block.
 * e.g., `@var`
 */
public interface DocTag {
    /** @return the full text content (e.g., "User $admin") */
    String getValue();
}

package phpTypes;

/**
 * This interface represents a variable in PHP, which can have a name and an optional PHPDoc block.
 */
public interface PhpVariable {
    /** @return PhpDocBlock object, or null */
    PhpDocBlock getDocBlock();
    /** @return the variable name (e.g., "$user").*/
    String getName();
}

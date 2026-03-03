package phpTypes;

import java.util.List;

/**
 * This interface represents a PHPDoc block, which can contain multiple documentation tags.
 * e.g., @var int $x
 */
public interface PhpDocBlock {
    /** @return a list of DocTag objects for a specific tag */
    List<DocTag> getTagsByName(String tagName);
}

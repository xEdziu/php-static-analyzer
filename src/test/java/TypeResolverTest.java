import org.junit.jupiter.api.Test;
import phpTypes.DocTag;
import phpTypes.PhpDocBlock;
import phpTypes.PhpType;
import phpTypes.PhpVariable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TypeResolverTest {

    // 1. Standard Type: /** @var User */ for $user → should return User.
    @Test
    void shouldReturnStandardType() {

        // Given
        PhpVariable var = mock(PhpVariable.class);
        PhpDocBlock doc = mock(PhpDocBlock.class);
        DocTag tag = mock(DocTag.class);

        when(var.getName()).thenReturn("$user");
        when(var.getDocBlock()).thenReturn(doc);
        when(doc.getTagsByName("var")).thenReturn(List.of(tag));
        when(tag.getValue()).thenReturn("User");

        // When
        PhpType result = TypeResolver.inferTypeFromDoc(var);

        // Then
        assertEquals("User", result.toString());
    }

    // 2. Union Type: /** @var string|int */ for $id → should return a UnionType of string and int.
    @Test
    void shouldReturnUnionType() {
        // Given
        PhpVariable var = mock(PhpVariable.class);
        PhpDocBlock doc = mock(PhpDocBlock.class);
        DocTag tag = mock(DocTag.class);

        when(var.getName()).thenReturn("$id");
        when(var.getDocBlock()).thenReturn(doc);
        when(doc.getTagsByName("var")).thenReturn(List.of(tag));
        when(tag.getValue()).thenReturn("string|int");

        // When
        PhpType result = TypeResolver.inferTypeFromDoc(var);

        // Then
        assertEquals("string|int", result.toString());
    }

    // 3. Named Tag: /** @var Logger $log */ for variable $log → should return Logger.
    @Test
    void shouldReturnNamedTagType() {
        // Given
        PhpVariable var = mock(PhpVariable.class);
        PhpDocBlock doc = mock(PhpDocBlock.class);
        DocTag tag = mock(DocTag.class);

        when(var.getName()).thenReturn("$log");
        when(var.getDocBlock()).thenReturn(doc);
        when(doc.getTagsByName("var")).thenReturn(List.of(tag));
        when(tag.getValue()).thenReturn("Logger $log");

        // When
        PhpType result = TypeResolver.inferTypeFromDoc(var);

        // Then
        assertEquals("Logger", result.toString());
    }

    // 4. Name Mismatch: /** @var Admin $adm */ for variable $guest → should return mixed.
    @Test
    void shouldReturnMixedOnNameMismatch() {

        // Given
        PhpVariable var = mock(PhpVariable.class);
        PhpDocBlock doc = mock(PhpDocBlock.class);
        DocTag tag = mock(DocTag.class);

        when(var.getName()).thenReturn("$guest");
        when(var.getDocBlock()).thenReturn(doc);
        when(doc.getTagsByName("var")).thenReturn(List.of(tag));
        when(tag.getValue()).thenReturn("Admin $adm");

        // When
        PhpType result = TypeResolver.inferTypeFromDoc(var);

        // Then
        assertEquals("mixed", result.toString());
    }

    // 5. Multiple Tags: /** @var int $id */ and /** @var string $name */ for $name → should return string.
    @Test
    void shouldReturnCorrectTypeFromMultipleTags() {
        // Given
        PhpVariable var = mock(PhpVariable.class);
        PhpDocBlock doc = mock(PhpDocBlock.class);
        DocTag tag1 = mock(DocTag.class);
        DocTag tag2 = mock(DocTag.class);

        when(var.getName()).thenReturn("$name");
        when(var.getDocBlock()).thenReturn(doc);
        when(doc.getTagsByName("var")).thenReturn(Arrays.asList(tag1, tag2));

        when(tag1.getValue()).thenReturn("int $id");
        when(tag2.getValue()).thenReturn("string $name");

        // When
        PhpType result = TypeResolver.inferTypeFromDoc(var);

        // Then
        assertEquals("string", result.toString());
    }

    // 6. Fallback: If no DocBlock exists -> return mixed.
    @Test
    void shouldReturnMixedWhenNoDocBlock() {
        // Given
        PhpVariable var = mock(PhpVariable.class);

        when(var.getDocBlock()).thenReturn(null);

        // When
        PhpType result = TypeResolver.inferTypeFromDoc(var);

        // Then
        assertEquals("mixed", result.toString());
    }

    // 7. Fallback: If no matching tag is found -> return mixed.
    @Test
    void shouldReturnMixedWhenNoMatchingTag() {
        // Given
        PhpVariable var = mock(PhpVariable.class);
        PhpDocBlock doc = mock(PhpDocBlock.class);

        when(var.getName()).thenReturn("$var");
        when(var.getDocBlock()).thenReturn(doc);
        when(doc.getTagsByName("var")).thenReturn(List.of());

        // When
        PhpType result = TypeResolver.inferTypeFromDoc(var);

        // Then
        assertEquals("mixed", result.toString());
    }
}
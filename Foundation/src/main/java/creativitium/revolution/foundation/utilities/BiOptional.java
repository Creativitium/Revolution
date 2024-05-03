package creativitium.revolution.foundation.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <h1>BiOptional</h1>
 * <p>Custom Optional-based object that accepts two objects of two different types.</p>
 * @param <X>
 * @param <Y>
 */
public class BiOptional<X, Y>
{
    private final X left;
    private final Y right;

    private BiOptional(X left, Y right)
    {
        this.left = left;
        this.right = right;
    }

    /**
     * Checks whether the left object is null.
     * @return  True if this isn't the case
     */
    public boolean isLeftPresent()
    {
        return left != null;
    }

    /**
     * Checks whether the right object is null.
     * @return  True if this isn't the case
     */
    public boolean isRightPresent()
    {
        return right != null;
    }

    /**
     * Checks whether the left and right objects are present.
     * @return  True if this is the case
     */
    public boolean areBothPresent()
    {
        return left != null && right != null;
    }

    /**
     * Checks whether the left and right objects are equal. Keep in mind that this will also return true if both are
     *  null, so if you want to compare objects you don't know are null, then you should also use {@code areBothPresent()}.
     * @return  True if both objects are equal.
     */
    public boolean areBothEqual()
    {
        return left == right;
    }

    /**
     * Gets the left object if it's present, otherwise throws a NoSuchElementException.
     * @throws NoSuchElementException   If the object isn't present
     * @return                          The object
     */
    public X getLeft()
    {
        if (left == null)
        {
            throw new NoSuchElementException("No value present");
        }

        return left;
    }

    /**
     * Gets the right object if it's present, otherwise throws a NoSuchElementException.
     * @throws NoSuchElementException   If the object isn't present
     * @return                          The object
     */
    public Y getRight()
    {
        if (right == null)
        {
            throw new NoSuchElementException("No value present");
        }

        return right;
    }

    /**
     * Performs an action if the left object is present
     * @param consumer  A consumer for the left object's type
     */
    public void ifLeftPresent(Consumer<X> consumer)
    {
        Objects.requireNonNull(consumer);

        if (isLeftPresent())
        {
            consumer.accept(left);
        }
    }

    /**
     * Performs an action if the right object is present
     * @param consumer  A Consumer for the right object's type
     */
    public void ifRightPresent(Consumer<Y> consumer)
    {
        Objects.requireNonNull(consumer);

        if (isRightPresent())
        {
            consumer.accept(right);
        }
    }

    /**
     * Performs an action if both objects are present
     * @param consumer  A Consumer for the left and right object types
     */
    public void ifBothPresent(BiConsumer<X, Y> consumer)
    {
        Objects.requireNonNull(consumer);

        if (areBothPresent())
        {
            consumer.accept(left, right);
        }
    }

    /**
     * Performs an action if both objects are present, otherwise performs the other action
     * @param consumer  A Consumer for the left and right object types
     * @param otherwise A Runnable
     */
    public void ifBothPresentOrElse(BiConsumer<X, Y> consumer, Runnable otherwise)
    {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(otherwise);

        if (areBothPresent())
        {
            consumer.accept(left, right);
        }
        else
        {
            otherwise.run();
        }
    }

    public static <X, Y> BiOptional<X, Y> ofNullables(@Nullable X left, @Nullable Y right)
    {
        return new BiOptional<>(left, right);
    }

    public static <X, Y> BiOptional<X, Y> of(@NotNull X left, @NotNull Y right)
    {
        Objects.requireNonNull(left);
        Objects.requireNonNull(right);

        return new BiOptional<>(left, right);
    }

    public static <X, Y> BiOptional<X, Y> fromOptionals(Optional<X> left, Optional<Y> right)
    {
        return new BiOptional<>(left.orElse(null), right.orElse(null));
    }
}

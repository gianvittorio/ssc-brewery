package guru.sfg.brewery.repositories.security.perms;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('beer.read')")
@Target(ElementType.METHOD)
public @interface BeerReadPermission {
}

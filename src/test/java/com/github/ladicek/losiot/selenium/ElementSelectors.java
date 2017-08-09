package com.github.ladicek.losiot.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public final class ElementSelectors {
    public By linkText(String text) {
        return new FilterOutAngularCrap(By.linkText(text));
    }

    public By buttonText(String text) {
        if (text.contains("'")) {
            // TODO
            throw new UnsupportedOperationException("Expected button text contains >>'<<, not implemented yet");
        }

        return new FilterOutAngularCrap(By.xpath("//button[contains(text(), '" + text + "')]"));
    }

    public By cssSelector(String selector) {
        return new FilterOutAngularCrap(By.cssSelector(selector));
    }

    public By tagName(String tagName) {
        return new FilterOutAngularCrap(By.tagName(tagName));
    }

    private static class FilterOutAngularCrap extends By {
        private final By delegate;

        public FilterOutAngularCrap(By delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            // TODO is there a better way to filter out the crap?
            return context.findElements(delegate)
                    .stream()
                    .filter(el -> el.getAttribute("disabled") == null)
                    .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return delegate.toString() + " (with Angular crap filtered out)";
        }
    }
}

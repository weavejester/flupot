# Flupot

A ClojureScript library for creating [React][] elements, in a similar
style to [Om][]'s `om.dom` namespace.

[react]: https://facebook.github.io/react/
[om]: https://github.com/omcljs/om

## Installation

Add the following to your project `:dependencies`:

    [flupot "0.4.0"]

## Basic Usage

Require the `flupot.dom` namespace:

```clojure
(ns flupot.example
  (:require [flupot.dom :as dom]))
```

There is a function for each DOM element:

```clojure
(dom/div (dom/p "Hello World"))
```

If the first argument is a map, it's used as the element's attributes:

```clojure
(dom/div {:class "foo"} (dom/p "Hello World"))
```

Special React options like `:key` are also supported.

The `class` attribute may be specified as a collection:

```clojure
(dom/p {:class ["foo" "bar"]} "Hello World")
```

And the `style` attribute may be specified as a map:

```clojure
(dom/p {:style {:color :red}} "Hello World")
```

If one of the child arguments is a seq, it's expanded out automatically:

```clojure
(dom/ul
 (for [i (range 5)]
   (dom/li {:key i} i)))
```

## Advanced Usage

Flupot can also be used to define your own wrappers around React
elements or similar libraries (such as [react-pixi][]). You probably
won't need to do this! But just in case...

There are two macros that allow you to do this: `defelement-fn` and
`defelement-macro`.

`defelement-fn` generates a function around an element method, with an
optional attribute transformation function:

```clojure
(require '[flupot.core :refer [defelement-fn]])

(defelement-fn span
  :elemf React.DOM.span
  :attrf cljs.core/clj->js)
```

This generates a function `span` that wraps `React.DOM.span`. The
attribute map is transformed with the `cljs.core/clj->js` function.

Complementing this is `defelement-macro`. This generates a macro that
will try to pre-compile as much as possible. If you give the macro the
same name as the function defined by `defelement-fn`, ClojureScript
will choose the macro when possible, and fall back to the function
otherwise.

```clojure
(require '[flupot.core :refer [defelement-macro]])

(defelement-macro span
  :elemf React.DOM.span
  :attrf cljs.core/clj->js
  :attrm flupot.core/clj->js)
```

This macro has third keyword argument, `:attrm`, which defines a
function that is applied inside the macro. The `flupot.core/clj->js`
function mimics `cljs.core/clj->js`, except that it attempts to
perform as much as the conversion as possible during compile time.

[react-pixi]: https://github.com/Izzimach/react-pixi/

## License

Copyright © 2016 James Reeves

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

package fj;

import static fj.FW.$;
import static fj.Function.curry;
import static fj.Function.uncurryF2;
import fj.control.parallel.Promise;
import fj.data.Array;
import fj.data.IterableW;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.data.Option;
import fj.data.Set;
import fj.data.Stream;
import fj.data.Tree;
import fj.data.TreeZipper;
import fj.data.Zipper;
import static fj.data.TreeZipper.treeZipper;
import static fj.data.Zipper.zipper;
import static fj.data.IterableW.wrap;
import static fj.data.Set.iterableSet;
import static fj.data.Tree.node;
import fj.pre.Ord;
import static fj.P.p;

/**
 * A wrapper for functions of arity 2, that decorates them with higher-order functions.
 */
public final class F2W<A, B, C> implements F2<A, B, C>, F<A, F<B, C>> {
  private final F2<A, B, C> f;

  private F2W(final F2<A, B, C> f) {
    this.f = f;
  }

  /**
   * Returns the undecorated function.
   *
   * @return The undecorated function that this wrapper wraps.
   */
  public F2<A, B, C> unwrap() {
    return f;
  }

  /**
   * Function application.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @return The result of the transformation.
   */
  public C f(final A a, final B b) {
    return f.f(a, b);
  }

  /**
   * Partial application.
   *
   * @param a The <code>A</code> to which to apply this function.
   * @return The function partially applied to the given argument.
   */
  public FW<B, C> f(final A a) {
    return $(curry(f).f(a));
  }

  /**
   * Curries this wrapped function to a wrapped function of arity-1 that returns another wrapped function.
   *
   * @return a wrapped function of arity-1 that returns another wrapped function.
   */
  public FW<A, F<B, C>> curryW() {
    return $(new F<A, F<B, C>>() {
      public F<B, C> f(final A a) {
        return F2W.this.f(a);
      }
    });
  }

  /**
   * Wraps a given function, decorating it with higher-order functions.
   *
   * @param f The function to wrap.
   * @return The wrapped function.
   */
  public static <A, B, C> F2W<A, B, C> $$(final F2<A, B, C> f) {
    return new F2W<A, B, C>(f);
  }

  /**
   * Wraps a given function, decorating it with higher-order functions.
   *
   * @param f The function to wrap.
   * @return The wrapped function.
   */
  public static <A, B, C> F2W<A, B, C> $$(final F<A, F<B, C>> f) {
    return $$(uncurryF2(f));
  }

  /**
   * Flips the arguments of this function.
   *
   * @return A new function with the arguments of this function flipped.
   */
  public F2W<B, A, C> flip() {
    return $$(Function.flip(f));
  }

  /**
   * Uncurries this function to a function on tuples.
   *
   * @return A new function that calls this function with the elements of a given tuple.
   */
  public FW<P2<A, B>, C> tuple() {
    return $(new F<P2<A, B>, C>() {
      public C f(final P2<A, B> p) {
        return F2W.this.f(p._1(), p._2());
      }
    });
  }

  /**
   * Promotes this function to a function on Arrays.
   *
   * @return This function promoted to transform Arrays.
   */
  public F2W<Array<A>, Array<B>, Array<C>> array() {
    return $$(new F2<Array<A>, Array<B>, Array<C>>() {
      public Array<C> f(final Array<A> a1, final Array<B> a2) {
        return a2.apply(curryW().mapArray().f(a1));
      }
    });
  }

  /**
   * Promotes this function to a function on Promises.
   *
   * @return This function promoted to transform Promises.
   */
  public F2W<Promise<A>, Promise<B>, Promise<C>> promise() {
    return $$(Promise.<A, B, C>liftM2(this));
  }

  /**
   * Promotes this function to a function on Iterables.
   *
   * @return This function promoted to transform Iterables.
   */
  public F2W<Iterable<A>, Iterable<B>, IterableW<C>> iterable() {
    return $$(IterableW.liftM2(this));
  }

  /**
   * Promotes this function to a function on Lists.
   *
   * @return This function promoted to transform Lists.
   */
  public F2W<List<A>, List<B>, List<C>> list() {
    return $$(List.liftM2(this));
  }

  /**
   * Promotes this function to a function on non-empty lists.
   *
   * @return This function promoted to transform non-empty lists.
   */
  public F2W<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>> nel() {
    return $$(new F2<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>>() {
      public NonEmptyList<C> f(final NonEmptyList<A> as, final NonEmptyList<B> bs) {
        return NonEmptyList.fromList(as.toList().bind(bs.toList(), f)).some();
      }
    });
  }

  /**
   * Promotes this function to a function on Options.
   *
   * @return This function promoted to transform Options.
   */
  public F2W<Option<A>, Option<B>, Option<C>> option() {
    return $$(Option.liftM2(this));
  }

  /**
   * Promotes this function to a function on Sets.
   *
   * @param o An ordering for the result of the promoted function.
   * @return This function promoted to transform Sets.
   */
  public F2W<Set<A>, Set<B>, Set<C>> set(final Ord<C> o) {
    return $$(new F2<Set<A>, Set<B>, Set<C>>() {
      public Set<C> f(final Set<A> as, final Set<B> bs) {
        Set<C> cs = Set.empty(o);
        for (final A a : as)
          for (final B b : bs)
            cs = cs.insert(F2W.this.f(a, b));
        return cs;
      }
    });
  }

  /**
   * Promotes this function to a function on Streams.
   *
   * @return This function promoted to transform Streams.
   */
  public F2W<Stream<A>, Stream<B>, Stream<C>> stream() {
    return $$(new F2<Stream<A>, Stream<B>, Stream<C>>() {
      public Stream<C> f(final Stream<A> as, final Stream<B> bs) {
        return as.bind(bs, f);
      }
    });
  }

  /**
   * Promotes this function to a function on Trees.
   *
   * @return This function promoted to transform Trees.
   */
  public F2W<Tree<A>, Tree<B>, Tree<C>> tree() {
    return $$(new F2<Tree<A>, Tree<B>, Tree<C>>() {
      public Tree<C> f(final Tree<A> as, final Tree<B> bs) {
        final F2<Tree<A>, Tree<B>, Tree<C>> self = this;
        return node(F2W.this.f(as.root(), bs.root()), new P1<Stream<Tree<C>>>() {
          public Stream<Tree<C>> _1() {
            return $$(self).stream().f(as.subForest()._1(), bs.subForest()._1());
          }
        });
      }
    });
  }

  /**
   * Promotes this function to zip two arrays, applying the function lock-step over both Arrays.
   *
   * @return A function that zips two arrays with this function.
   */
  public F2W<Array<A>, Array<B>, Array<C>> zipArray() {
    return $$(new F2<Array<A>, Array<B>, Array<C>>() {
      public Array<C> f(final Array<A> as, final Array<B> bs) {
        return as.zipWith(bs, f);
      }
    });
  }

  /**
   * Promotes this function to zip two iterables, applying the function lock-step over both iterables.
   *
   * @return A function that zips two iterables with this function.
   */
  public F2W<Iterable<A>, Iterable<B>, Iterable<C>> zipIterable() {
    return $$(new F2<Iterable<A>, Iterable<B>, Iterable<C>>() {
      public Iterable<C> f(final Iterable<A> as, final Iterable<B> bs) {
        return wrap(as).zipWith(bs, f);
      }
    });
  }

  /**
   * Promotes this function to zip two lists, applying the function lock-step over both lists.
   *
   * @return A function that zips two lists with this function.
   */
  public F2W<List<A>, List<B>, List<C>> zipList() {
    return $$(new F2<List<A>, List<B>, List<C>>() {
      public List<C> f(final List<A> as, final List<B> bs) {
        return as.zipWith(bs, f);
      }
    });
  }


  /**
   * Promotes this function to zip two streams, applying the function lock-step over both streams.
   *
   * @return A function that zips two streams with this function.
   */
  public F2W<Stream<A>, Stream<B>, Stream<C>> zipStream() {
    return $$(new F2<Stream<A>, Stream<B>, Stream<C>>() {
      public Stream<C> f(final Stream<A> as, final Stream<B> bs) {
        return as.zipWith(bs, f);
      }
    });
  }

  /**
   * Promotes this function to zip two non-empty lists, applying the function lock-step over both lists.
   *
   * @return A function that zips two non-empty lists with this function.
   */
  public F2W<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>> zipNel() {
    return $$(new F2<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>>() {
      public NonEmptyList<C> f(final NonEmptyList<A> as, final NonEmptyList<B> bs) {
        return NonEmptyList.fromList(as.toList().zipWith(bs.toList(), f)).some();
      }
    });
  }

  /**
   * Promotes this function to zip two sets, applying the function lock-step over both sets.
   *
   * @param o An ordering for the resulting set.
   * @return A function that zips two sets with this function.
   */
  public F2W<Set<A>, Set<B>, Set<C>> zipSet(final Ord<C> o) {
    return $$(new F2<Set<A>, Set<B>, Set<C>>() {
      public Set<C> f(final Set<A> as, final Set<B> bs) {
        return iterableSet(o, as.toStream().zipWith(bs.toStream(), f));
      }
    });
  }

  /**
   * Promotes this function to zip two trees, applying the function lock-step over both trees.
   * The structure of the resulting tree is the structural intersection of the two trees.
   *
   * @return A function that zips two trees with this function.
   */
  public F2W<Tree<A>, Tree<B>, Tree<C>> zipTree() {
    return $$(new F2<Tree<A>, Tree<B>, Tree<C>>() {
      public Tree<C> f(final Tree<A> ta, final Tree<B> tb) {
        final F2<Tree<A>, Tree<B>, Tree<C>> self = this;
        return node(F2W.this.f(ta.root(), tb.root()), new P1<Stream<Tree<C>>>() {
          public Stream<Tree<C>> _1() {
            return $$(self).zipStream().f(ta.subForest()._1(), tb.subForest()._1());
          }
        });
      }
    });
  }

  /**
   * Promotes this function to zip two zippers, applying the function lock-step over both zippers in both directions.
   * The structure of the resulting zipper is the structural intersection of the two zippers.
   *
   * @return A function that zips two zippers with this function.
   */
  public F2W<Zipper<A>, Zipper<B>, Zipper<C>> zipZipper() {
    return $$(new F2<Zipper<A>, Zipper<B>, Zipper<C>>() {
      public Zipper<C> f(final Zipper<A> ta, final Zipper<B> tb) {
        final F2W<Stream<A>, Stream<B>, Stream<C>> sf = $$(f).zipStream();
        return zipper(sf.f(ta.lefts(), tb.lefts()), f.f(ta.focus(), tb.focus()), sf.f(ta.rights(), tb.rights()));
      }
    });
  }

  /**
   * Promotes this function to zip two TreeZippers, applying the function lock-step over both zippers in all directions.
   * The structure of the resulting TreeZipper is the structural intersection of the two TreeZippers.
   *
   * @return A function that zips two TreeZippers with this function.
   */
  public F2W<TreeZipper<A>, TreeZipper<B>, TreeZipper<C>> zipTreeZipper() {
    return $$(new F2<TreeZipper<A>, TreeZipper<B>, TreeZipper<C>>() {
      public TreeZipper<C> f(final TreeZipper<A> ta, final TreeZipper<B> tb) {
        final F2<Stream<Tree<A>>, Stream<Tree<B>>, Stream<Tree<C>>> sf = $$(f).tree().zipStream();
        final
        F2<Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>>,
            Stream<P3<Stream<Tree<B>>, B, Stream<Tree<B>>>>,
            Stream<P3<Stream<Tree<C>>, C, Stream<Tree<C>>>>>
            pf =
            $$(new F2<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>,
                P3<Stream<Tree<B>>, B, Stream<Tree<B>>>,
                P3<Stream<Tree<C>>, C, Stream<Tree<C>>>>() {
              public P3<Stream<Tree<C>>, C, Stream<Tree<C>>> f(final P3<Stream<Tree<A>>, A, Stream<Tree<A>>> pa,
                                                               final P3<Stream<Tree<B>>, B, Stream<Tree<B>>> pb) {
                return p(tree().zipStream().f(pa._1(), pb._1()), f.f(pa._2(), pb._2()),
                         tree().zipStream().f(pa._3(), pb._3()));
              }
            }).zipStream();
        return treeZipper(tree().f(ta.p()._1(), tb.p()._1()), sf.f(ta.lefts(), tb.lefts()),
                          sf.f(ta.rights(), tb.rights()), pf.f(ta.p()._4(), tb.p()._4()));
      }
    });
  }
}

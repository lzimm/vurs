{-

Depends
* The Haskell Platform http://hackage.haskell.org/platform/
* Lastik http://hackage.haskell.org/package/Lastik

-}
module Build where

import qualified Lastik.Java.Javac as J
import qualified Lastik.Java.Javadoc as Jd
import qualified Lastik.Scala.Scalac as S
import qualified Lastik.Scala.Scaladoc as Sd
import Lastik.Runner
import Lastik.Output
import Lastik.Util
import Lastik.Directory
import Control.Monad
import Control.Arrow
import System.Cmd
import System.Exit
import System.Directory
import System.FilePath
import Lastik.Find
import Codec.Archive.Zip
import qualified Data.ByteString.Lazy as B
import Data.Digest.Pure.MD5
import Data.Digest.Pure.SHA

src = ["src" </> "main", "src" </> "package-info"]
deps = ["src" </> "deps-test"]
test = ["src" </> "test"]

build = "build"
javaco = build </> "classes" </> "javac"
depso = build </> "classes" </> "deps"
testo = build </> "classes" </> "test"
javadoco = build </> "javadoc"
scaladoco = build </> "scaladoc"
jardir = build </> "jar"
releasedir = build </> "release"
mavendir = build </> "maven"
etcdir = "etc"

resources = "resources"
cp = "classpath" ~?? [javaco, depso, testo, resources]
wt v = Just ("Functional Java " ++ v)
dt v = Just ("Functional Java " ++ v ++ " API Specification")
hd = Just "<div><p><em>Copyright 2008 - 2009 Tony Morris, Runar Bjarnason, Tom Adams, Brad Clow, Ricky Clarkson, Nick Partridge, Jason Zaugg</em></p>This software is released under an open source BSD licence.</div>"
ds = ".deps"
repo = "https://functionaljava.googlecode.com/svn/"
commitMessage = "\"Automated build\""

resolve = do e <- doesDirectoryExist ds
             unless e $ do mkdir ds
                           mapM_ (\d -> system ("wget -c --directory " ++ ds ++ ' ' : d)) k
  where
  k = map ("http://projects.tmorris.net/public/standards/artifacts/1.30/" ++) ["javadoc-style/javadoc-style.css", "scaladoc-style/script.js", "scaladoc-style/style.css"] ++ ["http://soft.tmorris.net/artifacts/package-list-j2se/1.5.0/package-list"]

type Version = String

readVersion :: IO Version
readVersion = readFile "version"

clean :: IO ()
clean = rmdir build

fullClean :: IO ()
fullClean = rmdir ds >> clean

javac' :: FilePath -> J.Javac
javac' d = J.javac {
  J.directory = Just d,
  J.deprecation = True,
  J.etc = Just "-Xlint:unchecked"
}

j :: J.Javac
j = javac' javaco

javac :: IO ExitCode
javac = j +->- src

repl :: IO ExitCode
repl = javac >>>> system ("scala -i initrepl " ++ cp)

javadoc' :: Version -> Jd.Javadoc
javadoc' v = Jd.javadoc {
  Jd.directory = Just javadoco,
  Jd.windowtitle = wt v,
  Jd.doctitle = dt v,
  Jd.header = hd,
  Jd.stylesheetfile = Just (ds </> "javadoc-style.css"),
  Jd.linkoffline = [("http://java.sun.com/j2se/1.5.0/docs/api", ds)],
  Jd.linksource = True
}

javadoc :: Version -> IO ExitCode
javadoc v = resolve >> javadoc' v ->- src

nosvn :: FilePather Bool
nosvn = fileName /=? ".svn"

nosvnf :: FilterPredicate
nosvnf = constant nosvn ?&&? isFile

archive :: IO ()
archive = do javac
             mkdir jardir
             writeArchive ([javaco, resources] `zip` repeat ".")
                         nosvn
                         nosvnf
                         [OptVerbose]
                         (jardir </> "functionaljava.jar")

buildAll :: IO ExitCode
buildAll = do v <- readVersion
              resolve
              archive
              javadoc v

maven :: IO ()
maven = do buildAll
           mkdir mavendir
           v <- readVersion
           forM_ [("javadoc", [javadoco]), ("sources", src), ("tests", test)] (\(n, f) ->
             writeHashArchive (map (flip (,) ".") f) nosvn nosvnf [OptVerbose] (mavendir </> "fj-" ++ v ++ '-' :  n ++ ".jar"))

svn :: String -> IO ExitCode
svn k = system ("svn " ++ k)

svn' :: [String] -> IO ExitCode
svn' = svn . join

release :: IO ExitCode
release = let k = build </> "functionaljava"
              updateVersion z = let (a, b) = second (show . (+1) . read . drop 1) (break (== '.') z)
                                in a ++ '.' : b
          in do buildAll
                mkdir k
                forM_ ([(1, javadoco), (2, jardir), (1, etcdir)] ++ map ((,) 0) src) (\(l, d) -> copyDir nosvn nosvnf l d k)
                mkdir releasedir
                writeHashArchive [(build, "functionaljava")] always always' [OptVerbose] (releasedir </> "functionaljava.zip")
                v <- readVersion
                svn' ["import ", build </> "functionaljava", " ", repo, "/artifacts/", v, " -m ", commitMessage]
                svn' ["import ", releasedir, " ", repo, "/artifacts/", v, "/release", " -m ", commitMessage]
                svn' ["copy ", repo, "trunk", " ", repo, "/tags/", v, " -m ", commitMessage]
                length v `seq` writeFile "version" (updateVersion v)
                svn ("commit version -m " ++ commitMessage)

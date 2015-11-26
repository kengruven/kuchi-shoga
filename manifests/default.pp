class { 'apt':
}

class { 'java':
  distribution => 'jre',
}

# $version "2" (default) gives branch "preview" (2.1.2, old)
class leiningen($user="vagrant") {
  class { "leiningen::install":
    user => $user,
    version => "stable"
  }
}
include leiningen

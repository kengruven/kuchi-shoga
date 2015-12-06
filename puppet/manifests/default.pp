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

class { 'postgresql::server':
  ip_mask_allow_all_users => '0.0.0.0/0',
  listen_addresses        => '*'
}

postgresql::server::db { 'kuchi_shoga':
  user     => 'kuchi_shoga_app',
  password => postgresql_password('kuchi_shoga_app', 'password123'),
}

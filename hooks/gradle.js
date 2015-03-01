module.exports = function(context) {
  var Q    = context.requireCordovaModule('q');
  var exec = require('child_process').exec;
  var dfd  = new Q.defer();

  exec('gradle getDeps', {
    cwd: context.opts.plugin.dir
  }, function() {
    dfd.resolve();
    console.log('cordova-safe - Gradle dependencies installed');
  });

  return dfd.promise;
};

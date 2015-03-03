module.exports = function(context) {
  var Q    = context.requireCordovaModule('q');
  var exec = require('child_process').exec;
  var dfd  = new Q.defer();

  console.log('Installing "com.disusered.safe" dependencies ');
  exec('gradle getDeps', {
    cwd: context.opts.plugin.dir
  }, function() {
    dfd.resolve();
  });

  return dfd.promise;
};

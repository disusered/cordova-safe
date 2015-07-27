exports.defineAutoTests = function() {
  describe('disusered plugin (cordova.plugins.disusered)', function() {
    it('should exist', function() {
      expect(window.cordova.plugins.disusered).toBeDefined();
    });
    it('should pass a test', function() {
      expect(window).toBeDefined();
    });
  });

  describe('safe object (cordova.plugins.disusered.safe)', function() {
    it('should exist', function() {
      expect(window.cordova.plugins.disusered.safe).toBeDefined();
    });

    it('should have an "encrypt" method', function() {
      expect(window.cordova.plugins.disusered.safe.encrypt).toBeDefined();
    });

    it('should have an "decrypt" method', function() {
      expect(window.cordova.plugins.disusered.safe.decrypt).toBeDefined();
    });
  });
};

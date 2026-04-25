module.exports = {
  root: true,
  env: {
    node: true
  },
  extends: [
    'plugin:vue/essential'
  ],
  parserOptions: {
    parser: 'babel-eslint'
  },
  rules: {
    'no-console': 'off',
    'no-debugger': 'off',
    'no-unused-vars': 'off',
    'vue/no-unused-components': 'off',
    // 关闭所有规则
    'vue/no-parsing-error': 'off',
    'vue/valid-template-root': 'off',
    'vue/no-template-key': 'off',
    'vue/no-unused-vars': 'off',
    'vue/require-v-for-key': 'off',
    'vue/valid-v-for': 'off',
    'vue/valid-v-if': 'off',
    'vue/valid-v-else': 'off',
    'vue/valid-v-else-if': 'off',
    'vue/valid-v-on': 'off',
    'vue/valid-v-once': 'off',
    'vue/valid-v-bind': 'off',
    'vue/valid-v-model': 'off',
    'vue/valid-v-html': 'off',
    'vue/valid-v-text': 'off',
    'vue/no-duplicate-attributes': 'off',
    'vue/no-use-v-if-with-v-for': 'off',
    'vue/valid-v-pre': 'off',
    'vue/valid-v-cloak': 'off',
    'vue/valid-v-slot': 'off',
    'vue/no-reserved-keys': 'off',
    'vue/no-shared-component-data': 'off',
    'vue/no-mutating-props': 'off',
    'vue/no-side-effects-in-computed-properties': 'off',
    'vue/return-in-computed-property': 'off',
    'vue/require-valid-default-prop': 'off',
    'vue/require-prop-types': 'off',
    'vue/no-custom-modifiers-on-v-model': 'off',
    'vue/no-multiple-template-root': 'off',
    'vue/no-v-model-argument': 'off',
  }
} 
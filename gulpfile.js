var gulp  = require('gulp')
  , gutil = require('gulp-util')
  , shell = require('gulp-shell')
  , less = require('gulp-less')
  , concat = require('gulp-concat')

gulp.task('cljsbuild', shell.task(['lein cljsbuild auto']))

gulp.task('stylesheets', function() {
  gulp.src('src/stylesheets/main.less')
    .pipe(less({ compress: true })).on('error', gutil.log)
    .pipe(concat('index.css'))
    .pipe(gulp.dest('./assets/stylesheets'))
})

gulp.task('watch', function() {
  gulp.watch([
    'index.html',
    'src/**/*.less'
  ], ['stylesheets'])
})

gulp.task('default', ['cljsbuild', 'stylesheets', 'watch'])

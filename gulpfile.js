var gulp  = require('gulp')
  , gutil = require('gulp-util')
  , shell = require('gulp-shell')
  , less = require('gulp-less')
  , concat = require('gulp-concat')

gulp.task('cljsbuild', shell.task(['lein cljsbuild auto']))

gulp.task('stylesheets', function() {
  gulp.src('resources/less/main.less')
    .pipe(less({ compress: true })).on('error', gutil.log)
    .pipe(concat('collab.css'))
    .pipe(gulp.dest('./dist/css'))
})

gulp.task('dist', function() {
  gulp.src([
    'bower_components/jquery/dist/jquery.js',
    'bower_components/bootstrap/dist/js/bootstrap.js',
    'bower_components/highlightjs/highlight.pack.js',
    'dist/js/collab.js'
  ]).pipe(concat('index.js'))
    .pipe(gulp.dest('dist/js'))

  gulp.src([
    'bower_components/bootstrap/dist/css/bootstrap.min.css',
    'dist/css/collab.css'
  ]).pipe(concat('index.css'))
    .pipe(gulp.dest('dist/css'))

  gulp.src('resources/images/*')
    .pipe(gulp.dest('./dist/images'))
  gulp.src('resources/fonts/*')
    .pipe(gulp.dest('./dist/fonts'))
  gulp.src('resources/index.html')
    .pipe(gulp.dest('./dist'))
})

gulp.task('watch', function() {
  gulp.watch([
    'resources/index.html',
    'resources/less/*',
    'dist/js/collab.js',
    'dist/js/collab.css'
  ], ['stylesheets', 'dist'])
})

gulp.task('default', [
  'cljsbuild',
  'stylesheets',
  'dist',
  'watch'
])

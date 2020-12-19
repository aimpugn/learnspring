yieldUnescaped '<!DOCTYPE html>'
html {
  include template: 'header.tpl'
  body {
    div(class: 'container') {
      div(class: 'navbar') {
        div(class: 'navbar-inner') {
          a(class: 'brand',
              href: 'http://beta.groovy-lang.org/docs/groovy-2.3.2/html/documentation/markup-template-engine.html',
              'Groovy - Template Engine docs')
          a(class: 'brand',
              href: 'hhttp://projects.spring.io/spring-boot/') {
            yield 'Spring Boot docs'
          }
        }
      }
      div("This is an application using Boot $bootVersion and Groovy templates $groovyVersion")
    }
    div {
      form (id:'userForm', action:'register', method:'post') {
        label (for:'email', 'Email')
        input (name:'email', type:'text', value:Hello)
        div (class:'form-actions') {
          input (type:'submit', value:'Submit')
        }
      }
    }
  }
}
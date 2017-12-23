function init() {
  Tabletop.init( { key: 'https://docs.google.com/spreadsheets/d/10VxZNjNi8xPxvGZt4p0I_2LL8iD-y_dykLCniF6LIig/edit?usp=sharing',
                   callback: function(data, tabletop) { 
                       console.log(data)
                   },
                   simpleSheet: true } )
}
window.addEventListener('DOMContentLoaded', init)
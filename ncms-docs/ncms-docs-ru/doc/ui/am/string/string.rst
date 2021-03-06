.. _am_string:

Строка (string)
===============

Строка - это самый широко используемый тип атрибута.
Этот атрибут позволяет задать произвольные текстовые данные
и отобразить их в контексте страниц.

Опции атрибута
--------------

.. figure:: img/string_img1.png

    Опции строкового атрибута

Отображать как
**************

* **поле** -- в разделе редактирования контента атрибут будет представлен в виде однострочного поля ввода.
* **зона ввода** -- атрибут будет отображен в зоне редактирования контента страницы как широкое, многострочное поле,
  удобное для ввода объемного текста.

Макс. длина (max length)
************************

Максимальное количество символов для значения атрибута. Ноль означает отсутствие ограничения.


Значение атрибута по умолчанию
******************************

Значение данного атрибута по умолчанию, если иное значение не задано редактором
страницы.

Placeholder
***********

Placeholder для данного элемента в интерфейсе редактирования страницы.

Использование в разметке
------------------------

**Тип значения атрибута:** `java.lang.String`

Безопасный вывод значения атрибута в html::

    ${asm('название атрибута')} или ${'название атрибута'.asm}

Вывод значения атрибута, допускающего html разметку::

    $!{asm('название атрибута')} или $!{'название атрибута'.asm}








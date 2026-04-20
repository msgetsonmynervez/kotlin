<!DOCTYPE html>

<html lang="en"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>Park App Settings</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<style data-purpose="custom-styles">
    /* Adding a blur effect to the background image to match the requested style */
    .bg-blur {
      backdrop-filter: blur(8px);
      -webkit-backdrop-filter: blur(8px);
    }
  </style>
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/></head>
<body class="bg-gray-900 text-white min-h-screen font-sans relative overflow-hidden">
<!-- BEGIN: Background Image -->
<!-- Using the provided canvas image as a blurred background -->
<div class="absolute inset-0 z-0">
<img alt="Background" class="w-full h-full object-cover opacity-60" src="https://lh3.googleusercontent.com/aida/ADBb0ugOBPJwii0iIxfDgiiDNT5xGnKkXaqFhvbTNT75sRI-9ZeTV68f6P1r2aiyT7a4rWRZAYKVnhPw6ckQhta2SXNwIBXviBursg6r_OQdAYAiTh0kokJ42K1VRY-1oAOBFWj-ftNjAyZMfZESGKfmAT54TtVqYjq0Bp6pL-ZZabRpB7lu51kQtYkYlWQpr3kwHRg7KA1bmjnkJLAtlqWVtMOxTtmqDI-VkiEUvtO3TZyIxesBOJhkGXYkD84"/>
<div class="absolute inset-0 bg-black/40"></div>
</div>
<!-- END: Background Image -->
<div class="relative z-10 flex flex-col h-screen">
<!-- BEGIN: Header -->
<header class="pt-12 pb-4 px-6 flex items-center justify-between bg-gradient-to-b from-black/80 to-transparent">
<button class="text-white hover:text-gray-300 transition-colors p-2 -ml-2" onclick="location.href='{{DATA:SCREEN:SCREEN_40}}'">
<i class="fa-solid fa-chevron-left text-2xl"></i>
</button>
<h1 class="text-2xl font-bold flex-1 text-center pr-8">Settings</h1>
</header>
<!-- END: Header -->
<!-- BEGIN: Main Content -->
<main class="flex-1 px-4 py-6 space-y-4 overflow-y-auto pb-24">
<!-- Settings List -->
<div class="space-y-4">
<!-- Sound Toggle -->
<div class="bg-black/60 bg-blur rounded-2xl p-4 flex items-center justify-between border border-white/10 shadow-lg">
<div class="flex items-center gap-4">
<div class="w-8 h-8 flex items-center justify-center text-xl">
<i class="fa-solid fa-volume-high"></i>
</div>
<span class="text-lg font-medium">Sound</span>
</div>
<label class="relative inline-flex items-center cursor-pointer">
<input checked="" class="sr-only peer" type="checkbox" value=""/>
<div class="w-14 h-8 bg-gray-600 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-7 after:w-7 after:transition-all peer-checked:bg-green-500"></div>
</label>
</div>
<!-- Voiceover Toggle -->
<div class="bg-black/60 bg-blur rounded-2xl p-4 flex items-center justify-between border border-white/10 shadow-lg">
<div class="flex items-center gap-4">
<div class="w-8 h-8 flex items-center justify-center text-xl">
<i class="fa-solid fa-microphone"></i>
</div>
<span class="text-lg font-medium">Voiceover</span>
</div>
<label class="relative inline-flex items-center cursor-pointer">
<input class="sr-only peer" type="checkbox" value=""/>
<div class="w-14 h-8 bg-gray-600 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-7 after:w-7 after:transition-all peer-checked:bg-green-500"></div>
</label>
</div>
<!-- Haptic Feedback Toggle -->
<div class="bg-black/60 bg-blur rounded-2xl p-4 flex items-center justify-between border border-white/10 shadow-lg">
<div class="flex items-center gap-4">
<div class="w-8 h-8 flex items-center justify-center text-xl">
<i class="fa-solid fa-mobile-screen-button"></i>
<!-- Assuming generic vibration icon to represent haptics if specific is unavailable -->
</div>
<span class="text-lg font-medium">Haptic Feedback</span>
</div>
<label class="relative inline-flex items-center cursor-pointer">
<input checked="" class="sr-only peer" type="checkbox" value=""/>
<div class="w-14 h-8 bg-gray-600 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-7 after:w-7 after:transition-all peer-checked:bg-green-500"></div>
</label>
</div>
<!-- About Link -->
<button class="w-full bg-black/60 bg-blur rounded-2xl p-4 flex items-center justify-between border border-white/10 shadow-lg hover:bg-black/80 transition-colors">
<div class="flex items-center gap-4">
<div class="w-8 h-8 flex items-center justify-center text-xl">
<i class="fa-solid fa-circle-info"></i>
</div>
<span class="text-lg font-medium">About Sterling's World</span>
</div>
<i class="fa-solid fa-chevron-right text-gray-400"></i>
</button>
</div>
</main>
<!-- END: Main Content -->
<!-- BEGIN: Bottom Navigation -->
<nav class="absolute bottom-0 w-full bg-[#1A1A1A] border-t border-[#333] px-6 py-4 pb-8 flex justify-between items-end rounded-t-3xl">
<!-- Nav Item 1 (Active) -->
<a class="flex flex-col items-center gap-1 group" href="#" onclick="location.href='{{DATA:SCREEN:SCREEN_40}}'">
<div class="w-10 h-10 flex items-center justify-center">
<span class="material-symbols-outlined text-gray-400 group-hover:text-gray-200 transition-colors">home</span>
</div>
<span class="text-xs text-gray-500 font-medium group-hover:text-gray-200 transition-colors">Home</span>
</a>
<!-- Nav Item 2 -->
<a class="flex flex-col items-center gap-1 group" href="#" onclick="location.href='{{DATA:SCREEN:SCREEN_85}}'">
<div class="w-10 h-10 flex items-center justify-center">
<span class="material-symbols-outlined text-gray-400 group-hover:text-gray-200 transition-colors">map</span>
</div>
<span class="text-xs text-gray-500 font-medium group-hover:text-gray-200 transition-colors">Map</span>
</a>
<!-- Nav Item 3 -->
<a class="flex flex-col items-center gap-1 group" href="#" onclick="location.href='{{DATA:SCREEN:SCREEN_139}}'">
<div class="w-10 h-10 flex items-center justify-center">
<span class="material-symbols-outlined text-gray-400 group-hover:text-gray-200 transition-colors">music_note</span>
</div>
<span class="text-xs text-gray-500 font-medium group-hover:text-gray-200 transition-colors">Music</span>
</a>
<!-- Nav Item 4 -->
<a class="flex flex-col items-center gap-1 group" href="#" onclick="location.href='{{DATA:SCREEN:SCREEN_140}}'">
<div class="w-10 h-10 flex items-center justify-center">
<span class="material-symbols-outlined text-gray-400 group-hover:text-gray-200 transition-colors">sports_esports</span>
</div>
<span class="text-xs text-gray-500 font-medium group-hover:text-gray-200 transition-colors">Games</span>
</a>
<!-- Nav Item 5 -->
<a class="flex flex-col items-center gap-1 group" href="#" onclick="location.href='{{DATA:SCREEN:SCREEN_142}}'">
<div class="w-10 h-10 flex items-center justify-center">
<span class="material-symbols-outlined text-gray-400 group-hover:text-gray-200 transition-colors">child_care</span>
</div>
<span class="text-xs text-gray-500 font-medium group-hover:text-gray-200 transition-colors">Kid Zone</span>
</a>
</nav>
<!-- END: Bottom Navigation -->
</div>
</body></html>
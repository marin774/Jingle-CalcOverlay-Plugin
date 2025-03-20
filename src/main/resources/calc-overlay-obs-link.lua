-- Jingle Calc Overlay OBS Link
-- Makes a 'Calc Overlay' source which updates the image as soon as new data comes through Ninjabrain Bot API.

obs = obslua

calc_overlay_dir = os.getenv("UserProfile"):gsub("\\", "/") .. "/.config/Jingle/calc-overlay-plugin/"

timers_activated = false
last_state = ''

image = obs.gs_image_file()
temp_image = obs.gs_image_file()

source_def = {}
source_def.id = "calc_overlay_source"
source_def.output_flags = bit.bor(obs.OBS_SOURCE_VIDEO, obs.OBS_SOURCE_CUSTOM_DRAW)

function image_source_load(image, file)
	obs.obs_enter_graphics();
	obs.gs_image_file_free(image);
	obs.obs_leave_graphics();

	obs.gs_image_file_init(image, file);

	obs.obs_enter_graphics();
	obs.gs_image_file_init_texture(image);
	obs.obs_leave_graphics();

	if not image.loaded then
        -- There's a chance this happens if ninb is updated rapidly (a lot of adjustments, spamming f3+c etc.)
		-- ignore it, a retry will happen next loop (:
	end
end

source_def.get_name = function()
	return "Calc Overlay"
end

source_def.create = function(source, settings)
	local data = {}

	return data
end

source_def.destroy = function(data)
    obs.obs_enter_graphics()

    obs.gs_image_file_free(image)
    obs.gs_image_file_free(temp_image)

    obs.obs_leave_graphics()
end

source_def.video_render = function(data, effect)
	if not image.texture then
		return;
	end

	effect = obs.obs_get_base_effect(obs.OBS_EFFECT_DEFAULT)

	obs.gs_blend_state_push()
	obs.gs_reset_blend_state()

	while obs.gs_effect_loop(effect, "Draw") do
		obs.obs_source_draw(image.texture, 0, 0, image.cx, image.cy, false);
	end

	obs.gs_blend_state_pop()
end

source_def.get_width = function(data)
	return 1250
end

source_def.get_height = function(data)
	return 550
end

function script_description()
	return "Adds a \"Calc Overlay\" source which displays Ninjabrain Bot data."
end

obs.obs_register_source(source_def)

---- Yoinked from jingle-obs-link.lua shoutout duncan ----

function read_first_line(filename)
    local rfile = io.open(filename, "r")
    if rfile == nil then
        return ""
    end
    io.input(rfile)
    local out = io.read()
    io.close(rfile)
    return out
end

function get_state_file_string()
    local success, result = pcall(read_first_line, calc_overlay_dir .. "obs-link-state")
    if success then
        return result
    end
    return nil
end


function script_update(settings)
    if timers_activated then
        return
    end

    timers_activated = true
    obs.timer_add(loop, 20)
end


function loop()
    local state = get_state_file_string()

    if (state == last_state or state == nil) then
        return
    end

    image_source_load(temp_image, calc_overlay_dir .. "calc-overlay.png")

    if temp_image ~= nil and temp_image.loaded then
        image = temp_image
        last_state = state
    end
end